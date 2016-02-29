package com.simple.excel;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;


public class CellTest
{

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
    final String REFERENCE_NEGATIVE_EXPRESSION = "=-A1";
    final String REFERENCE_EXPRESSION = "=A1";
    final String CHILDREN_DEPENDENCIES_EXPRESSION = "=A1+B11-B1+5-D2";
    final String WRONG_EXPRESSION = "=-A1s+3-A1+B11/2*B1";
    final String WRONG_EXPRESSION_2 = "=-A1+3-A1+B11/2*B1*";

    private final static Map<String, String> data = new HashMap();

    static
    {
        Cell cell1 = new Cell("A1");
        Cell cell2 = new Cell("B11");
        Cell cell3 = new Cell("B1");
        Cell cell4 = new Cell("C1");
        Cell cell5 = new Cell("C2");
        Cell cell6 = new Cell("D2");
        data.put(cell1.getCellId().toString(), "-3");
        data.put(cell2.getCellId().toString(), "-1");
        data.put(cell3.getCellId().toString(), "3");
        data.put(cell4.getCellId().toString(), "asd");
        data.put(cell5.getCellId().toString(), "23e");
        data.put(cell6.getCellId().toString(), "0");
    }

    @Test
    public void initTypeTest()
    {
        Cell cell;
        Set<CellId> childrenCellDependancies;

        cell = new Cell(0, 0);
        childrenCellDependancies = new TreeSet();
        cell.setStringValue(INTEGER_STRING);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.POSITIVE_NUMBER, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(INTEGER_STRING, cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(INTEGER_STRING_2);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.POSITIVE_NUMBER, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(INTEGER_STRING_2, cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(NEG_INTEGER_STRING);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.NEGATIVE_VALUE, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.NEGATIVE_VALUE.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(NEG_INTEGER_STRING_2);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.NEGATIVE_VALUE, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.NEGATIVE_VALUE.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(WRONG_INTEGER);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(REFERENCE);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(NEG_REFERENCE);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(REFERENCE_2);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(NEG_REFERENCE_2);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(WRONG_REFERENCE);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(STRING);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.STRING, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(STRING.substring(1), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(WRONG_STRING);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(STRING_NULL);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.NULL, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals("", cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(SIMPLE_EXPRESSION);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.getExpression().calculate(data);
//                .getStringValue();
        cell.calculateValue(data);
        assertEquals(CellType.EXPRESSION, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("4", cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(WRONG_EXPRESSION);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        cell.setStringValue(WRONG_EXPRESSION_2);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.calculateValue(data);
        assertEquals(CellType.ERROR, cell.getType());
        assertEquals(ErrorMessage.FORMAT_ERROR, cell.getError());
        assertNull(cell.getExpression());
        assertEquals(ErrorMessage.FORMAT_ERROR.getError(), cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        childrenCellDependancies = new TreeSet();
        childrenCellDependancies.add(new CellId("A1"));
        cell.setStringValue(REFERENCE_NEGATIVE_EXPRESSION);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.getExpression().calculate(data);
//        .getStringValue();
        cell.calculateValue(data);
        assertEquals(CellType.EXPRESSION, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("3", cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        childrenCellDependancies = new TreeSet();
        childrenCellDependancies.add(new CellId("A1"));
        cell.setStringValue(REFERENCE_EXPRESSION);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.getExpression().calculate(data);
//        .getStringValue();
        cell.calculateValue(data);
        assertEquals(CellType.EXPRESSION, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("-3", cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());

        cell = new Cell(0, 0);
        childrenCellDependancies = new TreeSet();
        childrenCellDependancies.add(new CellId("A1"));
        childrenCellDependancies.add(new CellId("B11"));
        childrenCellDependancies.add(new CellId("B1"));
        childrenCellDependancies.add(new CellId("D2"));
        cell.setStringValue(CHILDREN_DEPENDENCIES_EXPRESSION);
        cell.initType();
        cell.initChildrenCellDependancies();
        cell.getExpression().calculate(data);
//        .getStringValue();
        cell.calculateValue(data);
        assertEquals(CellType.EXPRESSION, cell.getType());
        assertEquals(ErrorMessage.NO_ERROR, cell.getError());
        assertNotNull(cell.getExpression());
        assertEquals("-2", cell.getValue());
        assertEquals(childrenCellDependancies, cell.getChildrenCellDependencies());
    }


}
