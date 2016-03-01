package com.simple.excel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CellIdTest
{

    final private String F33 = "F33";
    final int rowStringIndex = 32;
    final int columnStringIndex = 5;

    @Test
    public void parseCellId()
    {
        Pair<Character, Integer> cellIdLabel;
        Integer rowLabel;
        Character columnLabel;
        Cell.CellId cellId;
        Integer rowIndex;
        Integer columnIndex;

        // TODO: delete
//        cellIdLabel = CellId.indexToCellIdLabel(rowStringIndex, columnStringIndex);
//        rowLabel = cellIdLabel.getSecond();
//        columnLabel = cellIdLabel.getFirst();
//        assertEquals(new Integer(Integer.parseInt(F33.substring(1))), rowLabel);
//        assertEquals(new Character(F33.charAt(0)), columnLabel);
//        assertEquals(F33, new CellId(rowLabel, columnLabel).toString());
//        assertEquals(F33, new CellId(F33).toString());

        cellId = new Cell.CellId(F33);
        Pair<Integer, Integer> cellIndex = Cell.CellId.cellIdToIndexes(cellId);
        rowIndex = cellIndex.getFirst();
        columnIndex = cellIndex.getSecond();
        assertEquals(new Integer(rowStringIndex), rowIndex);
        assertEquals(new Integer(columnStringIndex), columnIndex);

        cellIdLabel = Cell.CellId.parseReference(F33);
        columnLabel = cellIdLabel.getFirst();
        rowLabel = cellIdLabel.getSecond();
        assertEquals((Integer) Integer.parseInt(F33.substring(1)), rowLabel);
        assertEquals((Character) F33.charAt(0), columnLabel);
    }

}
