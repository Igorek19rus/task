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

    final private Set<CellId> childrenCellDependencies = new TreeSet();
    final private Set<CellId> parentCellDependencies = new TreeSet();

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
            return;
        } else if (type.equals(CellType.ERROR)) {
            return;
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
            return;
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
        expression = new ExpressionImpl(originalValue);
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

    public static class CellId implements Comparable {
        private final char column;
        private final int row;

        public CellId(final String referenceString) {
            Pair<Character, Integer> cellId = CellId.parseReference(referenceString);
            this.column = cellId.getFirst();
            this.row = cellId.getSecond();
        }

        public CellId(final int row, final int column) {
            this.column = (char) (column + 65);
            this.row = row + 1;
        }

        public char getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }

        public String toString() {
            return column + String.valueOf(row);
        }

        public static Pair<Character, Integer> parseReference(final String stringReference) {
            Character column;
            int startRowIndex;
            if (stringReference.charAt(0) == Operation.SUBTRACTION.getOperation()) {
                column = stringReference.charAt(1);
                startRowIndex = 2;
            } else {
                column = stringReference.charAt(0);
                startRowIndex = 1;
            }
            return new Pair<Character, Integer>(Character.toUpperCase(column), Integer.parseInt(stringReference.substring(startRowIndex)));
        }

        /**
         * Convert the CellId values to the integer index values.
         *
         * @param cellId CellId value.
         * @return integer index values.
         */
        public static Pair<Integer, Integer> cellIdToIndexes(final CellId cellId) {
            return new Pair<Integer, Integer>(cellId.getRow() - 1, (int) cellId.getColumn() - 65);
        }

        /**
         * Convert row and column indexes to cellId object.
         *
         * @param row
         * @param column
         * @return
         */
        public static CellId indexesToCellId(final int row, final int column) {
            return new CellId(row, column);
        }

        /**
         * Convert integer index values to the CellId value.
         *
         * @param row    row.
         * @param column column.
         * @return CellId value.
         */
        public static Pair<Character, Integer> indexToCellIdLabel(final int row, final int column) {
            return new Pair<Character, Integer>((char) (column + 65), row + 1);
        }

        public int compareTo(final Object o) {
            CellId entry = (CellId) o;
            int rowResult = this.row - entry.getRow();
            if (rowResult != 0) {
                return (int) rowResult / Math.abs(rowResult);
            } else {
                return new Character(this.column).compareTo(new Character(entry.getColumn()));
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CellId cellId = (CellId) o;

            if (column != cellId.column) {
                return false;
            }
            return row == cellId.row;

        }

        @Override
        public int hashCode() {
            int result = (int) column;
            result = 31 * result + row;
            return result;
        }
    }
}
