package com.simple.excel;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by igor on 27.02.16.
 */
public class CellId implements Comparable
{
    private final char column;
    private final int row;

    public CellId(final int row, final char column) {
        this.row = row;
        this.column = column;
    }

    public CellId(final String referenceString) {
        Pair<Character, Integer> cellId = CellId.parseReference(referenceString);
        this.column = cellId.getFirst();
        this.row = cellId.getSecond();
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String toString(){
        return "" + column + row;
    }

    public static Pair<Character, Integer> parseReference(final String stringReference) {
        Character column;
        int startRowIndex;
        if (stringReference.charAt(0) == Operation.SUBSTRACTION.getOperation()) {
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
     * @param row row.
     * @param column column.
     * @return integer index values.
     */
//    public static Pair<Integer, Integer> cellIdToIndex(int row,  char column) {
//        return new Pair<Integer, Integer>((int) column - 65, row-1);
//    }

    /**
     * Convert the CellId values to the integer index values.
     * @param cellId CellId value.
     * @return integer index values.
     */
    public static Pair<Integer, Integer> cellIdToIndex(CellId cellId) {
        return new Pair<Integer, Integer>(cellId.getRow()-1, (int) cellId.getColumn() - 65);
    }

    /**

     * Convert integer index values to the CellId value.
     * @param row row.
     * @param column column.
     * @return CellId value.
     */
    public static Pair<Character, Integer> indexToCellIdLabel(int row, int column) {
        return new Pair<Character, Integer>((char) (column+65), row + 1);
    }

    public static void main(String[] args)
    {

        Set dependencies = new TreeSet();
        dependencies.add(new CellId("A1"));
        dependencies.add(new CellId("B11"));
        dependencies.add(new CellId("B1"));
        dependencies.add(new CellId("A11"));
//        Pair<Character, Integer> cellIdIndex = indexToCellIdLabel(0, 0);
//        System.out.println("0 0 -> " + cellIdIndex.getFirst() + " " + cellIdIndex.getSecond());
//
//        Pair<Character, Integer> cellIdIndex2 = indexToCellIdLabel(2, 5);
//        System.out.println("2 5 -> " + cellIdIndex2.getFirst() + " " + cellIdIndex2.getSecond());

//        Pair<Integer, Integer> cellIdLabel = cellIdToIndex('A', 1);
//        System.out.println("A 1 -> " + cellIdLabel.getFirst() + " " + cellIdLabel.getSecond());
//        Pair<Integer, Integer> cellIdLabel2 = cellIdToIndex('C', 6);
//        System.out.println("C 6 -> " + cellIdLabel2.getFirst() + " " + cellIdLabel2.getSecond());

//        CellId c = new CellId(1,'A');
//        System.out.print(c);
    }

    public int compareTo(Object o)
    {
        CellId entry = (CellId) o;
        int rowResult = this.row - entry.getRow();
        if(rowResult != 0) {
            return (int) rowResult / Math.abs( rowResult );
        } else {
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
