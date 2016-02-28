package com.simple.excel;

import java.util.*;

public class Cell {

    private String stringValue = "";
    private String value = "";
    private final CellId cellId;
    private CellType type = CellType.NULL;
    private ErrorMessage error = ErrorMessage.NO_ERROR;
    private FormulaImpl2 expression;
    private Set<CellId> childrenCellDependancies = new TreeSet();
    private Set<CellId> parentCellDependancies = new TreeSet();
    public Cell(int row, char col) {
        cellId = new CellId(row, col);
    }
    public Cell(final String cellId) {
        this.cellId = new CellId(cellId);
    }

    public Cell(int row, int col) {
        Pair<Character, Integer> cellIdLabel = CellId.indexToCellIdLabel(row, col);
        cellId = new CellId(cellIdLabel.getFirst()+cellIdLabel.getSecond().toString());
    }

    public ErrorMessage getError()
    {
        return error;
    }

    public void setError(ErrorMessage error)
    {
        this.error = error;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null) throw new NullPointerException();
        this.value = value;
    }


    public CellId getCellId() {
        return cellId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    /**
     * Get cells linked to the current cell.
     * @return cells linked to the current cell.
     */
    public Set<CellId> getChildrenCellDependancies() {
        return childrenCellDependancies;
    }

    /**
     * Get depended cells from the current cell.
     * @return depended cells from the current cell.
     */
    public Set<CellId> getParentCellDependancies() {
        return parentCellDependancies;
    }

    /**
     * Return the displayed value.
     * @return the displayed value.
     */
    public void calculateValue() {
        if (type.equals(CellType.NULL)) {
            value = "";
            return;
        } else if (type.equals(CellType.EXPRESSION)) {
            try {
                Integer resInt = Integer.parseInt(expression.getCalculated());
                value = resInt.toString();
                return;
            } catch (Exception e) {
                setErrorType(ErrorMessage.FORMAT_ERROR);
                return;
            }
        } else if (type.equals(CellType.POSITIVE_NUMBER)) {
            Integer resInt = Integer.parseInt(stringValue);
            if (resInt > 0) {
                value = resInt.toString();
            } else {
                setErrorType(ErrorMessage.NEGATIVE_VALUE);
                return;
            }
        } else if (type.equals(CellType.STRING)) {
            value = stringValue.substring(1);
            return;
        } else if (type.equals(CellType.ERROR)) {
            return;
        }
    }

    /**
     * Set cell type by parsing string value.
     */
    public void initType() {
        if (stringValue.isEmpty()){
            initNullTypeCell();
            return;
        }
        try {
            switch (stringValue.charAt(0))
            {
                case '\'': {
                    initStringTypeCell();
                    return;
                }
                case '=': {
                    initExpressionTypeCell();
                    return;
                }
            }

            if (Integer.parseInt(stringValue)>= 0) {
                initPositiveNumberTypeCell();
                return;
            }
            setErrorType(ErrorMessage.NEGATIVE_VALUE);
            return;
        } catch (NumberFormatException ex) {
            setErrorType(ErrorMessage.FORMAT_ERROR);
            return;
        } catch(FormatErrorException ex) {
            setErrorType(ErrorMessage.FORMAT_ERROR);
            return;
        }
    }
    
    private void initNullTypeCell() {
        expression = null;
        type = CellType.NULL;
        error = ErrorMessage.NO_ERROR;
    }
    
    private void initStringTypeCell() {
        expression = null;
        error = ErrorMessage.NO_ERROR;
        type = CellType.STRING;
        value = stringValue;
    }
    
    private void initExpressionTypeCell() {
        expression = new FormulaImpl2(stringValue);
        initChildrenCellDependancies();
        error = ErrorMessage.NO_ERROR;
        type = CellType.EXPRESSION;
    }
    
    private void initPositiveNumberTypeCell() {
        expression = null;
        type = CellType.POSITIVE_NUMBER;
    }

    public void setErrorType (final ErrorMessage error) {
        expression = null;
        type = CellType.ERROR;
        this.error = error;
        value = error.getError();
    }

    public void initChildrenCellDependancies() {
        if (expression != null) {
            Set<CellId> first = FormulaImpl2.parseDependencies(stringValue);
            for (CellId cellId : first) {
                childrenCellDependancies.add(cellId);
            }
        }
    }

    public FormulaImpl2 getExpression() {
        return expression;
    }

    public void setExpression(FormulaImpl2 expression) {
        this.expression = expression;
    }

    public void showChildrenDependencies() {
        System.out.print(cellId + ": ");
        for (CellId c : childrenCellDependancies) {
            System.out.print("{" + c + "}");
        }
        System.out.println();
    }

    public void showParentDependencies() {
        System.out.print(cellId + ": ");
        for (CellId c : parentCellDependancies) {
            System.out.print("{" + c + "}");
        }
        System.out.println();
    }

    public static void main(String[] args) {

    }
}
