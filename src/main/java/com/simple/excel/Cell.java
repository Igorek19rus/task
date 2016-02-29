package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class Cell
{
    private static Logger log = LogManager.getLogger(Table.class);

    private String stringValue = "";
    private String value = "";
    private final CellId cellId;
    private CellType type = CellType.NULL;
    private ErrorMessage error = ErrorMessage.NO_ERROR;
    private Expression expression;
    private Set<CellId> childrenCellDependencies = new TreeSet();
    private Set<CellId> parentCellDependencies = new TreeSet();

//    public Cell(int row, char col)
//    {
//        cellId = new CellId(row, col);
//    }

    public Cell(final String cellId)
    {
        this.cellId = new CellId(cellId);
    }

    public Cell(int row, int col)
    {
        Pair<Character, Integer> cellIdLabel = CellId.indexToCellIdLabel(row, col);
        cellId = new CellId(cellIdLabel.getFirst() + cellIdLabel.getSecond().toString());
    }

    public ErrorMessage getError()
    {
        return error;
    }

    public void setError(ErrorMessage error)
    {
        this.error = error;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        if(value == null)
        {
            throw new NullPointerException("Can not set null value.");
        }
        this.value = value;
    }

    public CellId getCellId()
    {
        return cellId;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }

    public CellType getType()
    {
        return type;
    }

    public void setType(CellType type)
    {
        this.type = type;
    }

    /**
     * Get cells linked to the current cell.
     *
     * @return cells linked to the current cell.
     */
    public Set<CellId> getChildrenCellDependencies()
    {
        return childrenCellDependencies;
    }

    /**
     * Get depended cells from the current cell.
     *
     * @return depended cells from the current cell.
     */
    public Set<CellId> getParentCellDependencies()
    {
        return parentCellDependencies;
    }

    /**
     * Return the displayed value.
     *
     * @return the displayed value.
     */
    public void calculateValue(final Map<String, String> data)
    {
        if(type.equals(CellType.NULL))
        {
            value = "";
            return;
        }
        else if(type.equals(CellType.EXPRESSION))
        {
            try
            {
                if (expression.getCalculated() == null)
                {
                    expression.calculate(data);
                }

                Integer resInt = Integer.parseInt(expression.getCalculated().getStringValue());
                value = resInt.toString();
                return;
            }
            catch(Exception e)
            {
                log.info("Error integer parsing : " + expression.getCalculated().getStringValue());
                setErrorType(ErrorMessage.FORMAT_ERROR);
                return;
            }
        }
        else if(type.equals(CellType.POSITIVE_NUMBER))
        {
            Integer resInt = Integer.parseInt(stringValue);
            if(resInt > 0)
            {
                value = resInt.toString();
            }
            else
            {
                setErrorType(ErrorMessage.NEGATIVE_VALUE);
                return;
            }
        }
        else if(type.equals(CellType.STRING))
        {
            value = stringValue.substring(1);
            return;
        }
        else if(type.equals(CellType.ERROR))
        {
            return;
        }
    }

    /**
     * Set cell type by parsing string value.
     */
    public void initType()
    {
        if(stringValue.trim().isEmpty())
        {
            initNullTypeCell();
            return;
        }
        try
        {
            switch(stringValue.charAt(0))
            {
                case '\'':
                {
                    initStringTypeCell();
                    return;
                }
                case '=':
                {
                    initExpressionTypeCell();
                    return;
                }
            }


            try
            {
                if(Integer.parseInt(stringValue) >= 0)
                {
                    initPositiveNumberTypeCell();
                    return;
                }
            }catch(NumberFormatException ex)
            {
                throw new FormatErrorException(ex);
            }
            setErrorType(ErrorMessage.NEGATIVE_VALUE);
            return;
        }
        catch(FormatErrorException ex)
        {
            log.info("Error parsing : " + stringValue);
            setErrorType(ErrorMessage.FORMAT_ERROR);
            return;
        }
    }

    private void initNullTypeCell()
    {
        stringValue = "";
        expression = null;
        type = CellType.NULL;
        error = ErrorMessage.NO_ERROR;
    }

    private void initStringTypeCell()
    {
        expression = null;
        error = ErrorMessage.NO_ERROR;
        type = CellType.STRING;
        value = stringValue;
    }

    private void initExpressionTypeCell()
    {
        expression = new ExpressionImpl(stringValue);
        initChildrenCellDependancies();
        error = ErrorMessage.NO_ERROR;
        type = CellType.EXPRESSION;
    }

    private void initPositiveNumberTypeCell()
    {
        expression = null;
        type = CellType.POSITIVE_NUMBER;
    }

    public void setErrorType(final ErrorMessage error)
    {
        expression = null;
        type = CellType.ERROR;
        this.error = error;
        value = error.getError();
    }

    public void initChildrenCellDependancies()
    {
        if(expression != null)
        {
            Set<CellId> first = expression.parseDependencies();
            for(CellId cellId : first)
            {
                childrenCellDependencies.add(cellId);
            }
        }
    }

    public Expression getExpression()
    {
        return expression;
    }

    public void setExpression(ExpressionImpl expression)
    {
        this.expression = expression;
    }

    public void showChildrenDependencies()
    {
        System.out.print(cellId + ": ");
        for(CellId c : childrenCellDependencies)
        {
            System.out.print("{" + c + "}");
        }
        System.out.println();
    }

    public void showParentDependencies()
    {
        System.out.print(cellId + ": ");
        for(CellId c : parentCellDependencies)
        {
            System.out.print("{" + c + "}");
        }
        System.out.println();
    }

    public static void main(String[] args)
    {

    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        Cell cell = (Cell) o;

        return cellId.equals(cell.cellId);

    }

    @Override
    public int hashCode()
    {
        return cellId.hashCode();
    }
}
