package com.simple.excel;

import org.junit.Test;


import static org.junit.Assert.*;

public class TableTest
{
    public static String SIMPLE_TABLE = "3\t5\n"
            + "8\t=1-A1\t'rt\t=E1\t=B2\n"
            + "5\t=A1+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t=A1\t=C3\t-9";

    public static String SIMPLE_NULL_TABLE = "3\t5\n"
            + " \t \t \t \t \n"
            + " \t \t \t \t \n"
            + " \t \t \t \t ";

    public static String CYCLE_TABLE = "3\t5\n"
            + "=B2\t1\t2\t=B1\t4\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9";

    public static String WRONG_TABLE = "3\t5\n"
            + "=B2\t1\t2\t=B1\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9";

    public static String WRONG_TABLE_2 = "3\t5\n"
            + "=B2\t1\t2\t=B1\t4\t6\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9";

    public static String WRONG_TABLE_3 = "3\t5\n"
            + "=B2\t1\t2\t=B1\t4\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9\n"
            + "=A1\t=A3\t7\t=8\t9";

    @Test (expected = FormatErrorException.class)
    public void parseTableLackColumn() {
        Table table = new Table(WRONG_TABLE);
    }

    @Test (expected = FormatErrorException.class)
    public void parseTableExcessColumn() {
        Table table = new Table(WRONG_TABLE_2);
    }

    @Test (expected = FormatErrorException.class)
    public void parseTableExcessRow() {
        Table table = new Table(WRONG_TABLE_3);
    }

    @Test
    public void initTableTest()
    {
        Table table;
        Table tableToCompare;
        Cell curCell;

        table = new Table(SIMPLE_TABLE);
        tableToCompare = new Table(SIMPLE_NULL_TABLE);

        constructChildrenDependenciesOfSimpleTable(tableToCompare);
        constructSimpleTableCellTypes(tableToCompare);
        table.buildDependencyTrees();
        assertEquals(SIMPLE_TABLE.substring(4), table.getMatrix().printStringValueTable());
        for(int i = 0; i < table.getMatrix().getRowSize(); i++)
        {
            for(int j = 0; j < table.getMatrix().getColumnSize(); j++)
            {
                assertEquals(table.getMatrix().getElement(i, j).getChildrenCellDependencies(), tableToCompare.getMatrix().getElement(i, j).getChildrenCellDependencies());
                assertEquals(table.getMatrix().getElement(i, j).getType(), tableToCompare.getMatrix().getElement(i, j).getType());
            }
        }

        table = new Table(CYCLE_TABLE);
        tableToCompare = new Table(CYCLE_TABLE);
        constructCycleTableCellTypes(tableToCompare);
        assertEquals(CYCLE_TABLE.substring(4), table.getMatrix().printStringValueTable());
        for(int i = 0; i < table.getMatrix().getRowSize(); i++)
        {
            for(int j = 0; j < table.getMatrix().getColumnSize(); j++)
            {
                assertEquals(table.getMatrix().getElement(i, j).getType(), tableToCompare.getMatrix().getElement(i, j).getType());
            }
        }
        constructChildrenDependenciesOfCycleTable(tableToCompare);
        for(int i = 0; i < table.getMatrix().getRowSize(); i++)
        {
            for(int j = 0; j < table.getMatrix().getColumnSize(); j++)
            {
                assertEquals(table.getMatrix().getElement(i, j).getType(), tableToCompare.getMatrix().getElement(i, j).getType());
                assertEquals(table.getMatrix().getElement(i, j).getChildrenCellDependencies(), tableToCompare.getMatrix().getElement(i, j).getChildrenCellDependencies());
            }
        }
    }

    private void constructSimpleTableCellTypes(final Table table)
    {
        Cell curCell;
        curCell = table.getMatrix().getElement(0, 0);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(0, 1);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(0, 2);
        curCell.setType(CellType.STRING);
        curCell = table.getMatrix().getElement(0, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(0, 4);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 0);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(1, 1);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 2);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 4);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 0);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(2, 1);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 2);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 4);
        curCell.setType(CellType.ERROR);
    }

    private void constructCycleTableCellTypes(final Table table)
    {
        Cell curCell;
        curCell = table.getMatrix().getElement(0, 0);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(0, 1);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(0, 2);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(0, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(0, 4);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(1, 0);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(1, 1);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 2);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(1, 4);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 0);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 1);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 2);
        curCell.setType(CellType.POSITIVE_NUMBER);
        curCell = table.getMatrix().getElement(2, 3);
        curCell.setType(CellType.EXPRESSION);
        curCell = table.getMatrix().getElement(2, 4);
        curCell.setType(CellType.POSITIVE_NUMBER);
    }

    private void constructChildrenDependenciesOfSimpleTable(final Table table)
    {
        Cell curCell;

        curCell = table.getMatrix().getElement(new CellId("B1"));
        curCell.getChildrenCellDependencies().add(new CellId("A1"));

        curCell = table.getMatrix().getElement(new CellId("D1"));
        curCell.getChildrenCellDependencies().add(new CellId("E1"));

        curCell = table.getMatrix().getElement(new CellId("E1"));
        curCell.getChildrenCellDependencies().add(new CellId("B2"));

        curCell = table.getMatrix().getElement(new CellId("B2"));
        curCell.getChildrenCellDependencies().add(new CellId("A1"));
        curCell.getChildrenCellDependencies().add(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new CellId("E2"));

        curCell = table.getMatrix().getElement(new CellId("C2"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));

        curCell = table.getMatrix().getElement(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));
        curCell.getChildrenCellDependencies().add(new CellId("B3"));

        curCell = table.getMatrix().getElement(new CellId("E2"));
        curCell.getChildrenCellDependencies().add(new CellId("B3"));

        curCell = table.getMatrix().getElement(new CellId("B3"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));

        curCell = table.getMatrix().getElement(new CellId("C3"));
        curCell.getChildrenCellDependencies().add(new CellId("A1"));

        curCell = table.getMatrix().getElement(new CellId("D3"));
        curCell.getChildrenCellDependencies().add(new CellId("C3"));
    }

    private void constructChildrenDependenciesOfCycleTable(final Table table)
    {
        Cell curCell;

        curCell = table.getMatrix().getElement(new CellId("A1"));
        curCell.getChildrenCellDependencies().add(new CellId("B2"));

        curCell = table.getMatrix().getElement(new CellId("D1"));
        curCell.getChildrenCellDependencies().add(new CellId("B1"));

        curCell = table.getMatrix().getElement(new CellId("B2"));
        curCell.getChildrenCellDependencies().add(new CellId("C2"));
        curCell.getChildrenCellDependencies().add(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new CellId("E2"));

        curCell = table.getMatrix().getElement(new CellId("C2"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));

        curCell = table.getMatrix().getElement(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));
        curCell.getChildrenCellDependencies().add(new CellId("B3"));

        curCell = table.getMatrix().getElement(new CellId("E2"));
        curCell.getChildrenCellDependencies().add(new CellId("B3"));

        curCell = table.getMatrix().getElement(new CellId("A3"));
        curCell.getChildrenCellDependencies().add(new CellId("A1"));

        curCell = table.getMatrix().getElement(new CellId("B3"));
        curCell.getChildrenCellDependencies().add(new CellId("A3"));
    }

    public void childrenDependenciesTableTest()
    {

    }


    public void tableCellTypeTest()
    {

    }
}
