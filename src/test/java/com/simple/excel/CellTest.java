package com.simple.excel;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.*;


public class CellTest {

    final String INTEGER_STRING = "3";
    final String INTEGER_STRING_2 = "32";
    final String NEG_INTEGER_STRING = "-3";
    final String NEG_INTEGER_STRING_2 = "-352";
    final String WRONG_INTEGER = "32d";
    final String REFERENCE = "A1";
    final String NEG_REFERENCE = "-A1";
    final String REFERENCE_2 = "A12";
    final String NEG_REFERENCE_2 = "-A22";
    final String WRONG_REFERENCE = "A32d";
    final String STRING = "'cse423fd32d";
    final String WRONG_STRING = "d32d";
    final String STRING_NULL = "";
    final String SIMPLE_EXPRESSION = "=5+3-4";
    final String REFERENCE_NEGATIVE_EXPRESSION = "=-B1";
    final String REFERENCE_EXPRESSION = "=B1";
    final String CHILDREN_DEPENDENCIES_EXPRESSION = "=B1*D2/3";
    final String WRONG_EXPRESSION = "=-A1s+3-A1+B11/2*B1";
    final String WRONG_EXPRESSION_2 = "=-A1+3-A1+B11/2*B1*";

    private final static Map<CellId, Cell> data = new HashMap();

    static {
        Cell cell1 = new Cell("A1");
        cell1.setOriginalValue("-3");
        cell1.initType();
        cell1.calculateValue(data);
        data.put(cell1.getCellId(), cell1);

        Cell cell2 = new Cell("B11");
        cell2.setOriginalValue("-1");
        cell2.initType();
        cell2.calculateValue(data);
        data.put(cell2.getCellId(), cell2);

        Cell cell3 = new Cell("B1");
        cell3.setOriginalValue("3");
        cell3.initType();
        cell3.calculateValue(data);
        data.put(cell3.getCellId(), cell3);

        Cell cell4 = new Cell("C1");
        cell4.setOriginalValue("asd");
        cell4.initType();
        cell4.calculateValue(data);
        data.put(cell4.getCellId(), cell4);

        Cell cell5 = new Cell("C2");
        cell5.setOriginalValue("23e");
        cell5.initType();
        cell5.calculateValue(data);
        data.put(cell5.getCellId(), cell5);

        Cell cell6 = new Cell("D2");
        cell6.setOriginalValue("0");
        cell6.initType();
        cell6.calculateValue(data);
        data.put(cell6.getCellId(), cell6);

    }

    @Test
    public void initTypeTest() {
        Cell cell;
        Set<CellId> childrenCellDependancies;
        Set<CellId> childrenCellDependanciesResult;

        cell = new Cell(0, 0);
        childrenCellDependancies = new HashSet();
        cell.setOriginalValue(INTEGER_STRING);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.POSITIVE_NUMBER, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(INTEGER_STRING, cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(INTEGER_STRING_2);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.POSITIVE_NUMBER, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(INTEGER_STRING_2, cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(NEG_INTEGER_STRING);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.NEGATIVE_VALUE, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.NEGATIVE_VALUE.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(NEG_INTEGER_STRING_2);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.NEGATIVE_VALUE, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.NEGATIVE_VALUE.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(WRONG_INTEGER);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(REFERENCE);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(NEG_REFERENCE);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(REFERENCE_2);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(NEG_REFERENCE_2);
        cell.initType();
        cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(WRONG_REFERENCE);
        cell.initType();
        cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(STRING);
        cell.initType();
        cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.STRING, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(STRING.substring(1), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(WRONG_STRING);
        cell.initType();
        cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(STRING_NULL);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.NULL, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals("", cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(SIMPLE_EXPRESSION);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.getExpression().calculate(data);
        cell.calculateValue(data);
        assertEquals(Cell.CellType.EXPRESSION, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("4", cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(WRONG_EXPRESSION);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        cell.setOriginalValue(WRONG_EXPRESSION_2);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.calculateValue(data);
        assertEquals(Cell.CellType.ERROR, cell.getType());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(Cell.ErrorMessage.FORMAT_ERROR.getError(), cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        childrenCellDependancies = new HashSet();
        childrenCellDependancies.add(new CellId("B1"));
        cell.setOriginalValue(REFERENCE_NEGATIVE_EXPRESSION);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.getExpression().calculate(data);
        cell.calculateValue(data);
        assertEquals(Cell.CellType.EXPRESSION, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("-3", cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        childrenCellDependancies = new HashSet();
        childrenCellDependancies.add(new CellId("B1"));
        cell.setOriginalValue(REFERENCE_EXPRESSION);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.getExpression().calculate(data);
        cell.calculateValue(data);
        assertEquals(Cell.CellType.EXPRESSION, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("3", cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);

        cell = new Cell(0, 0);
        childrenCellDependancies = new HashSet();
        childrenCellDependancies.add(new CellId("D2"));
        childrenCellDependancies.add(new CellId("B1"));
        cell.setOriginalValue(CHILDREN_DEPENDENCIES_EXPRESSION);
        cell.initType();
        childrenCellDependanciesResult = cell.initChildrenCellIdDependencies();
        cell.getExpression().calculate(data);
        cell.calculateValue(data);
        assertEquals(Cell.CellType.EXPRESSION, cell.getType());
        assertEquals(Cell.ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("0", cell.getResultValue());
        assertEquals(childrenCellDependancies, childrenCellDependanciesResult);
    }
}
