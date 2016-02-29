package com.simple.excel;

import java.util.Set;
import java.util.TreeSet;

public class CellId implements Comparable
{
    private final char column;
    private final int row;

    public CellId(final int row, final char column)
    {
        this.row = row;
        this.column = column;
    }

    public CellId(final String referenceString)
    {
        Pair<Character, Integer> cellId = CellId.parseReference(referenceString);
        this.column = cellId.getFirst();
        this.row = cellId.getSecond();
    }

    public char getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public String toString()
    {
        return "" + column + row;
    }

    public static Pair<Character, Integer> parseReference(final String stringReference)
    {
        Character column;
        int startRowIndex;
        if(stringReference.charAt(0) == Operation.SUBSTRACTION.getOperation())
        {
            column = stringReference.charAt(1);
            startRowIndex = 2;
        }
        else
        {
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
    public static Pair<Integer, Integer> cellIdToIndex(CellId cellId)
    {
        return new Pair<Integer, Integer>(cellId.getRow() - 1, (int) cellId.getColumn() - 65);
    }

    /**
     * Convert integer index values to the CellId value.
     *
     * @param row    row.
     * @param column column.
     * @return CellId value.
     */
    public static Pair<Character, Integer> indexToCellIdLabel(int row, int column)
    {
        return new Pair<Character, Integer>((char) (column + 65), row + 1);
    }

    public int compareTo(Object o)
    {
        CellId entry = (CellId) o;
        int rowResult = this.row - entry.getRow();
        if(rowResult != 0)
        {
            return (int) rowResult / Math.abs(rowResult);
        }
        else
        {
            return new Character(this.column).compareTo(new Character(entry.getColumn()));
        }
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

        CellId cellId = (CellId) o;

        if(column != cellId.column)
        {
            return false;
        }
        return row == cellId.row;

    }

    @Override
    public int hashCode()
    {
        int result = (int) column;
        result = 31 * result + row;
        return result;
    }
}
