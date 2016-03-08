package com.simple.excel;

/**
 * Cell identification.
 */
public class CellId{

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

    /**
     * Parse reference label.
     * @param stringReference reference label.
     * @return a pair of int row and char column label.
     */
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
