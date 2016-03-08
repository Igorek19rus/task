package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

/**
 * SimpleExcel cell.
 */
public class Cell {

    private static Logger log = LogManager.getLogger(SimpleExcel.class);

    private String originalValue = "";
    private String resultValue = "";

    private final CellId cellId;

    private CellType type;

    private ErrorMessage error;
    private Expression expression;

    final private Set<Cell> childrenCellDependencies = new HashSet();
    final private Set<Cell> parentCellDependencies = new HashSet();

    public Cell(final String cellId) {
        this.cellId = new CellId(cellId);
        initNullTypeCell();
    }

    public Cell(final int row, final int col) {
        Pair<Character, Integer> cellIdLabel = CellId.indexToCellIdLabel(row, col);
        cellId = new CellId(cellIdLabel.getFirst() + cellIdLabel.getSecond().toString());
        initNullTypeCell();
    }

    /**
     * Get possible error message by parsing or calculation.
     * @return get error message.
     */
    public ErrorMessage getError() {
        return error;
    }

    /**
     * Get output cell value.
     *
     * @return output cell value.
     */
    public String getResultValue() {
        return resultValue;
    }

    public CellId getCellId() {
        return cellId;
    }

    /**
     * Get input cell value.
     *
     * @return input cell value.
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Set input string value.
     *
     * @param originalValue output string value.
     */
    public void setOriginalValue(final String originalValue) {
        this.originalValue = originalValue.trim();
    }

    public CellType getType() {
        return type;
    }

    public void setType(final CellType type) {
        this.type = type;
    }

    /**
     * Get cells linked to the current cell.
     *
     * @return cells linked to the current cell.
     */
    public Set<Cell> getChildrenCellDependencies() {
        return childrenCellDependencies;
    }

    /**
     * Get depended cells from the current cell.
     *
     * @return depended cells from the current cell.
     */
    public Set<Cell> getParentCellDependencies() {
        return parentCellDependencies;
    }

    /**
     * Calculate cell value. This value will be the result value.
     *
     * @param expressionDependencyValues dependency expression data values (cell id, cell).
     */
    public void calculateValue(final Map<CellId, Cell> expressionDependencyValues) {
        if (type.equals(CellType.NULL)) {
            resultValue = "";
        } else if (type.equals(CellType.EXPRESSION)) {
            try {
                if (expression.getCalculated() == null) {
                    expression.calculate(expressionDependencyValues);
                }
                Integer resInt = Integer.parseInt(expression.getCalculated().getStringValue());
                resultValue = resInt.toString();
            } catch (Exception e) {
                log.info("Error by parsing : " + expression.getExpression(), e);
                setErrorType(ErrorMessage.FORMAT_ERROR);
            }
        } else if (type.equals(CellType.POSITIVE_NUMBER)) {
            Integer resInt = Integer.parseInt(originalValue);
            if (resInt >= 0) {
                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);
                resultValue = resInt.toString();
            } else {
                setErrorType(ErrorMessage.NEGATIVE_VALUE);
            }
        } else if (type.equals(CellType.STRING)) {
            resultValue = originalValue.substring(1).trim();
        }
    }

    /**
     * Set cell type by parsing string value.
     */
    @SuppressWarnings("PMD")
    public void initType() {
        if (originalValue.trim().isEmpty()) {
            initNullTypeCell();
            return;
        }
        try {
            switch (originalValue.charAt(0)) {
                case '\'': {
                    initStringTypeCell();
                    return;
                }
                case '=': {
                    initExpressionTypeCell();
                    return;
                }
            }
            try {
                if (Integer.parseInt(originalValue) >= 0) {
                    initPositiveNumberTypeCell();
                    return;
                }
            } catch (NumberFormatException ex) {
                throw new FormatErrorException("Error integer parsing.", ex);
            }
            setErrorType(ErrorMessage.NEGATIVE_VALUE);
        } catch (FormatErrorException ex) {
            log.info("Error parsing : " + originalValue);
            setErrorType(ErrorMessage.FORMAT_ERROR);
        } catch (CellOperationException ex) {
            log.info("Error cell operation : " + originalValue);
            setErrorType(ErrorMessage.FORMAT_ERROR);
        }
    }

    /**
     * Initialize the null type cell.
     */
    private void initNullTypeCell() {
        originalValue = "";
        expression = null;
        type = CellType.NULL;
        error = ErrorMessage.NO_ERROR;
    }

    /**
     * Initialize the string type cell.
     */
    private void initStringTypeCell() {
        expression = null;
        error = ErrorMessage.NO_ERROR;
        type = CellType.STRING;
        resultValue = originalValue;
    }

    /**
     * Initialize the expression type cell.
     */
    private void initExpressionTypeCell() {
        expression = new ExpressionImpl(originalValue, new ParserImpl());
        error = ErrorMessage.NO_ERROR;
        type = CellType.EXPRESSION;
    }

    private void initPositiveNumberTypeCell() {
        expression = null;
        type = CellType.POSITIVE_NUMBER;
    }

    /**
     * Initialize the error type cell.
     * @param error error message.
     */
    public void setErrorType(final ErrorMessage error) {
        expression = null;
        type = CellType.ERROR;
        this.error = error;
        resultValue = error.getError();
    }

    /**
     * Initialize the children cell dependencies by parsing the expression.
     * @return the set of cell id's which depended on the cell or empty set where no expression.
     */
    public Set<CellId> initChildrenCellIdDependencies() {
        if (expression == null) {
            return new HashSet();
        }
        Set<CellId> result;
        try {
            result = expression.parseDependencies();
        } catch (FormatErrorException ex) {
            log.info(ex);
            setErrorType(ErrorMessage.FORMAT_ERROR);
            return new HashSet();
        }
        return result;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final ExpressionImpl expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cell cell = (Cell) o;

        return cellId.equals(cell.cellId);

    }

    @Override
    public int hashCode() {
        return cellId.hashCode();
    }

    /**
     * Define the cell's error message.
     */
    public enum ErrorMessage {
        NEGATIVE_VALUE("#negative_number"),
        CYCLE_DEPENDENCIES("#cycle_dependencies"),
        FORMAT_ERROR("#format_error"),
        NO_ERROR("");

        private String error;

        ErrorMessage(final String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * Define the cell's type.
     */
    enum CellType {
        NULL,
        POSITIVE_NUMBER,
        STRING,
        EXPRESSION,
        ERROR
    }
}
