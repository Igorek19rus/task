package test;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cell {
    public enum Type {
        NULL,
        POSITIVE_NUMBER,
        STRING,
        EXPRESSION,
        ERROR;
    }

    public enum ErrorMessage {
        NEGATIVE_VALUE("#negative_value"),
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

    private String originalValue;
    private String resultValue;

    private final int row;
    private final int col;

    private Type type;

    Expression expression;
    ErrorMessage error;

    final private Set<Cell> childrenCellDependencies = new HashSet<Cell>();
    final private Set<Cell> parentCellDependencies = new HashSet<Cell>();

    public Cell(int row, int col, String originalValue) {
        this.row = row;
        this.col = col;
        this.originalValue = originalValue.trim();

        initCell(originalValue);
    }

    private void initCell(String originalValue) {
        if (originalValue.isEmpty()) {
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
            setErrorType(ErrorMessage.FORMAT_ERROR);
        }
    }

    private void initNullTypeCell() {
        resultValue = "";
        expression = null;
        type = Type.NULL;
        error = ErrorMessage.NO_ERROR;
    }

    private void initStringTypeCell() {
        expression = null;
        error = ErrorMessage.NO_ERROR;
        type = Type.STRING;
        resultValue = originalValue;
    }

    private void initExpressionTypeCell() {
        expression = new Expression(originalValue);
        //TODO: children dependencies will be analyzed in table class
        type = Type.EXPRESSION;
        error = ErrorMessage.NO_ERROR;
    }

    private void initPositiveNumberTypeCell() {
        expression = null;
        type = Type.POSITIVE_NUMBER;
    }

    public void setErrorType(final ErrorMessage error) {
        expression = null;
        type = Type.ERROR;
        this.error = error;
        resultValue = error.getError();
    }

    public void calculateValue(final Map<Cell, String> data) {
        if (type.equals(Type.NULL)) {
            resultValue = "";
            return;
        } else if (type.equals(Type.EXPRESSION)) {
            try {
                if (expression.getCalculated() == null) {
                    //TODO: data has changed!!!
                    expression.calculate(data);
                }

                Double resInt = Double.parseDouble(expression.getCalculated().getStringValue());
                resultValue = formatDouble(resInt);
                return;
            } catch (Exception e) {
                setErrorType(ErrorMessage.FORMAT_ERROR);
                return;
            }
        } else if (type.equals(Type.POSITIVE_NUMBER)) {
            Double resInt = Double.parseDouble(originalValue);
            if (resInt > 0) {
                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);
                resultValue = formatDouble(resInt);
            } else {
                setErrorType(ErrorMessage.NEGATIVE_VALUE);
                return;
            }
        } else if (type.equals(Type.STRING)) {
            resultValue = originalValue.substring(1).trim();
            return;
        } else if (type.equals(Type.ERROR)) {
            return;
        }
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getResultValue() {
        return resultValue;
    }

    public Type getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }

    public ErrorMessage getError() {
        return error;
    }

    public static int[] getPositions(String cellId) {
        Character column;
        int startRowIndex;
        if (cellId.charAt(0) == Operation.SUBTRACTION.getOperation()) {
            column = cellId.charAt(1);
            startRowIndex = 2;
        } else {
            column = cellId.charAt(0);
            startRowIndex = 1;
        }
        column = Character.toUpperCase(column);
        int row = Integer.parseInt(cellId.substring(startRowIndex));
        return new int[]{row - 1, (int) column - 65};
    }

    public static String formatDouble(final Double dbl) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        return format.format(dbl);
    }

    public Set<Cell> getChildrenCellDependencies() {
        return childrenCellDependencies;
    }

    public Set<Cell> getParentCellDependencies() {
        return parentCellDependencies;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
