package com.simple.excel;

import org.junit.Test;


import static org.junit.Assert.*;

public class SimpleExcelTest {

    public static String SIMPLE_TABLE = "3\t5\n"
            + "8\t=1-A1\t'rt\t=E1\t=-B2\n"
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

    @Test(expected = FormatErrorException.class)
    public void parseTableLackColumn() {
        SimpleExcel simpleExcel = new SimpleExcel(WRONG_TABLE);
    }

    @Test(expected = FormatErrorException.class)
    public void parseTableExcessColumn() {
        SimpleExcel simpleExcel = new SimpleExcel(WRONG_TABLE_2);
    }

    @Test(expected = FormatErrorException.class)
    public void parseTableExcessRow() {
        SimpleExcel simpleExcel = new SimpleExcel(WRONG_TABLE_3);
    }

    @Test
    public void initTableTest() {
        SimpleExcel simpleExcel;
        SimpleExcel simpleExcelToCompare;
        String resultSimpleTable = "8\t-7\trt\t-26\t-26\n5\t26\t6\t12\t6\n6\t6\t8\t8\t#negative_number";
        String resultCycleTable = "#cycle_dependencies\t1\t2\t1\t4\n" +
                "5\t#cycle_dependencies\t#cycle_dependencies\t#cycle_dependencies\t#cycle_dependencies\n" +
                "#cycle_dependencies\t#cycle_dependencies\t7\t8\t9";

        simpleExcel = new SimpleExcel(SIMPLE_TABLE);
        simpleExcelToCompare = new SimpleExcel(SIMPLE_NULL_TABLE);

        constructChildrenDependenciesOfSimpleTable(simpleExcelToCompare);
        constructSimpleTableCellTypes(simpleExcelToCompare);
        simpleExcel.buildChildrenDependencyTrees();
        simpleExcel.buildParentDependencyTrees();
        simpleExcel.resolveCycleDependencies();
        simpleExcel.calculationTable();
        for (int i = 0; i < simpleExcel.getTable().getRowSize(); i++) {
            for (int j = 0; j < simpleExcel.getTable().getColumnSize(); j++) {
                assertEquals(simpleExcel.getTable().getElement(i, j).getChildrenCellDependencies(), simpleExcelToCompare.getTable().getElement(i, j).getChildrenCellDependencies());
                assertEquals(simpleExcel.getTable().getElement(i, j).getType(), simpleExcelToCompare.getTable().getElement(i, j).getType());
            }
        }

        assertEquals(resultSimpleTable, simpleExcel.getTable().printTable());

        simpleExcel = new SimpleExcel(CYCLE_TABLE);
        simpleExcelToCompare = new SimpleExcel(CYCLE_TABLE);
        constructCycleTableCellTypes(simpleExcelToCompare);
        simpleExcel.buildChildrenDependencyTrees();
        for (int i = 0; i < simpleExcel.getTable().getRowSize(); i++) {
            for (int j = 0; j < simpleExcel.getTable().getColumnSize(); j++) {
                assertEquals(simpleExcel.getTable().getElement(i, j).getType(), simpleExcelToCompare.getTable().getElement(i, j).getType());
            }
        }
        constructChildrenDependenciesOfCycleTable(simpleExcelToCompare);
        for (int i = 0; i < simpleExcel.getTable().getRowSize(); i++) {
            for (int j = 0; j < simpleExcel.getTable().getColumnSize(); j++) {
                assertEquals(simpleExcel.getTable().getElement(i, j).getType(), simpleExcelToCompare.getTable().getElement(i, j).getType());
                assertEquals(simpleExcel.getTable().getElement(i, j).getChildrenCellDependencies(), simpleExcelToCompare.getTable().getElement(i, j).getChildrenCellDependencies());
            }
        }

        simpleExcel.buildParentDependencyTrees();
        simpleExcel.resolveCycleDependencies();
        simpleExcel.calculationTable();
        assertEquals(resultCycleTable, simpleExcel.getTable().printTable());
    }

    private void constructSimpleTableCellTypes(final SimpleExcel simpleExcel) {
        Cell curCell;

        curCell = simpleExcel.getTable().getElement(0, 0);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(0, 1);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(0, 2);
        curCell.setType(Cell.CellType.STRING);
        curCell = simpleExcel.getTable().getElement(0, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(0, 4);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 0);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(1, 1);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 2);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 4);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 0);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(2, 1);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 2);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 4);
        curCell.setType(Cell.CellType.ERROR);
    }

    private void constructCycleTableCellTypes(final SimpleExcel simpleExcel) {
        Cell curCell;

        curCell = simpleExcel.getTable().getElement(0, 0);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(0, 1);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(0, 2);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(0, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(0, 4);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(1, 0);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(1, 1);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 2);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(1, 4);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 0);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 1);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 2);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
        curCell = simpleExcel.getTable().getElement(2, 3);
        curCell.setType(Cell.CellType.EXPRESSION);
        curCell = simpleExcel.getTable().getElement(2, 4);
        curCell.setType(Cell.CellType.POSITIVE_NUMBER);
    }

    private void constructChildrenDependenciesOfSimpleTable(final SimpleExcel simpleExcel) {
        Cell curCell;

        curCell = simpleExcel.getTable().getElement(new CellId("B1"));
        curCell.getChildrenCellDependencies().add(new Cell("A1"));

        curCell = simpleExcel.getTable().getElement(new CellId("D1"));
        curCell.getChildrenCellDependencies().add(new Cell("E1"));

        curCell = simpleExcel.getTable().getElement(new CellId("E1"));
        curCell.getChildrenCellDependencies().add(new Cell("B2"));

        curCell = simpleExcel.getTable().getElement(new CellId("B2"));
        curCell.getChildrenCellDependencies().add(new Cell("A1"));
        curCell.getChildrenCellDependencies().add(new Cell("D2"));
        curCell.getChildrenCellDependencies().add(new Cell("E2"));

        curCell = simpleExcel.getTable().getElement(new CellId("C2"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));

        curCell = simpleExcel.getTable().getElement(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));
        curCell.getChildrenCellDependencies().add(new Cell("B3"));

        curCell = simpleExcel.getTable().getElement(new CellId("E2"));
        curCell.getChildrenCellDependencies().add(new Cell("B3"));

        curCell = simpleExcel.getTable().getElement(new CellId("B3"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));

        curCell = simpleExcel.getTable().getElement(new CellId("C3"));
        curCell.getChildrenCellDependencies().add(new Cell("A1"));

        curCell = simpleExcel.getTable().getElement(new CellId("D3"));
        curCell.getChildrenCellDependencies().add(new Cell("C3"));
    }

    private void constructChildrenDependenciesOfCycleTable(final SimpleExcel simpleExcel) {
        Cell curCell;

        curCell = simpleExcel.getTable().getElement(new CellId("A1"));
        curCell.getChildrenCellDependencies().add(new Cell("B2"));

        curCell = simpleExcel.getTable().getElement(new CellId("D1"));
        curCell.getChildrenCellDependencies().add(new Cell("B1"));

        curCell = simpleExcel.getTable().getElement(new CellId("B2"));
        curCell.getChildrenCellDependencies().add(new Cell("C2"));
        curCell.getChildrenCellDependencies().add(new Cell("D2"));
        curCell.getChildrenCellDependencies().add(new Cell("E2"));

        curCell = simpleExcel.getTable().getElement(new CellId("C2"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));

        curCell = simpleExcel.getTable().getElement(new CellId("D2"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));
        curCell.getChildrenCellDependencies().add(new Cell("B3"));

        curCell = simpleExcel.getTable().getElement(new CellId("E2"));
        curCell.getChildrenCellDependencies().add(new Cell("B3"));

        curCell = simpleExcel.getTable().getElement(new CellId("A3"));
        curCell.getChildrenCellDependencies().add(new Cell("A1"));

        curCell = simpleExcel.getTable().getElement(new CellId("B3"));
        curCell.getChildrenCellDependencies().add(new Cell("A3"));
    }
}
