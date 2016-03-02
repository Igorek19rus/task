package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Table cell.
 */
public class Cell {

    private static Logger log = LogManager.getLogger(Table.class);

    private String originalValue = "";
    private String resultValue = "";

    private final CellId cellId;

    private CellType type;

    private ErrorMessage error;
    private Expression expression;

    final private Set<CellId> childrenCellDependencies = new HashSet();
    final private Set<CellId> parentCellDependencies = new HashSet();

    public Cell(final String cellId) {
        this.cellId = new CellId(cellId);
        initNullTypeCell();
    }

    public Cell(final int row, final int col) {
        Pair<Character, Integer> cellIdLabel = CellId.indexToCellIdLabel(row, col);
        cellId = new CellId(cellIdLabel.getFirst() + cellIdLabel.getSecond().toString());
        initNullTypeCell();
    }

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
    public Set<CellId> getChildrenCellDependencies() {
        return childrenCellDependencies;
    }

    /**
     * Get depended cells from the current cell.
     *
     * @return depended cells from the current cell.
     */
    public Set<CellId> getParentCellDependencies() {
        return parentCellDependencies;
    }

    /**
     * Calculate cell value.
     *
     * @param expressionDependencyValues dependency expression data values.
     */
    public void calculateValue(final Map<String, String> expressionDependencyValues) {
        if (type.equals(CellType.NULL)) {
            resultValue = "";
            return;
        } else if (type.equals(CellType.EXPRESSION)) {
            try {
                if (expression.getCalculated() == null) {
                    expression.calculate(expressionDependencyValues);
                }
                Double resInt = Double.parseDouble(expression.getCalculated().getStringValue());
                resultValue = formatDouble(resInt);
                return;
            } catch (Exception e) {
                log.info("Error integer parsing : " + expression.getCalculated().getStringValue(), e);
                setErrorType(ErrorMessage.FORMAT_ERROR);
                return;
            }
        } else if (type.equals(CellType.POSITIVE_NUMBER)) {
            Double resInt = Double.parseDouble(originalValue);
            if (resInt > 0) {
                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);
                resultValue = formatDouble(resInt);
            } else {
                setErrorType(ErrorMessage.NEGATIVE_VALUE);
                return;
            }
        } else if (type.equals(CellType.STRING)) {
            resultValue = originalValue.substring(1).trim();
        }
    }

    public static String formatDouble(final Double dbl) {
        Integer result = dbl.intValue();
        return result.toString();
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
        }
    }

    private void initNullTypeCell() {
        originalValue = "";
        expression = null;
        type = CellType.NULL;
        error = ErrorMessage.NO_ERROR;
    }

    private void initStringTypeCell() {
        expression = null;
        error = ErrorMessage.NO_ERROR;
        type = CellType.STRING;
        resultValue = originalValue;
    }

    private void initExpressionTypeCell() {
        expression = new ExpressionImpl(originalValue, new ParserImpl());
        initChildrenCellDependencies();
        error = ErrorMessage.NO_ERROR;
        type = CellType.EXPRESSION;
    }

    private void initPositiveNumberTypeCell() {
        expression = null;
        type = CellType.POSITIVE_NUMBER;
    }

    public void setErrorType(final ErrorMessage error) {
        expression = null;
        type = CellType.ERROR;
        this.error = error;
        resultValue = error.getError();
    }

    public void initChildrenCellDependencies() {
        if (expression != null) {
            Set<CellId> first = expression.parseDependencies();
            for (CellId cellId : first) {
                childrenCellDependencies.add(cellId);
            }
        }
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

    enum CellType {
        NULL,
        POSITIVE_NUMBER,
        STRING,
        EXPRESSION,
        ERROR;
    }
}
