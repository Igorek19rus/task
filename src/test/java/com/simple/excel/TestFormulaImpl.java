package com.simple.excel;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.*;


/**
 * Created by igor on 27.02.16.
 */
public class TestFormulaImpl {


    final private String EXPRESSION_1 = "=5+3-4";
    final private String EXPRESSION_2 = "=-A1+3-A1+B11/2*B1";
    final private String EXPRESSION_3 = "=-3";
    final private String EXPRESSION_4 = "=-A1";
    final private String EXPRESSION_5 = "=A1";
    final private String EXPRESSION_6 = "=3";
    final private String WRONG_EXPRESSION = "=-A1s+3-A1+B11/2*B1";
    final private String WRONG_EXPRESSION_2 = "=-A1+3-A1+B11/2*B1*";
    final private String WRONG_EXPRESSION_3 = "=-A1/0";

    final private String INTEGER_STRING = "3";
    final private String INTEGER_STRING_2 = "32";
    final private String NEG_INTEGER_STRING = "-3";
    final private String NEG_INTEGER_STRING_2 = "-352";
    final private String WRONG_INTEGER = "32d";
    final private String REFERENCE = "A1";
    final private String NEG_REFERENCE = "-A1";
    final private String REFERENCE_2 = "A12";
    final private String NEG_REFERENCE_2 = "-A22";
    final private String WRONG_REFERENCE = "A32d";
    final private String STRING = "'cse423fd32d";
    final private String WRONG_STRING = "d32d";
    final private String STRIN_NULL = "";

    private final static Map<String, String> data = new HashMap();

    static {
        Cell cell1 = new Cell("A1");
        Cell cell2 = new Cell("B11");
        Cell cell3 = new Cell("B1");
        Cell cell4 = new Cell("C1");
        Cell cell5 = new Cell("C2");
        Cell cell6 = new Cell("D2");
        data.put(cell1.getCellId().toString(),"-3");
        data.put(cell2.getCellId().toString(),"-1");
        data.put(cell3.getCellId().toString(),"3");
        data.put(cell4.getCellId().toString(),"asd");
        data.put(cell5.getCellId().toString(),"23e");
        data.put(cell6.getCellId().toString(),"0");
    }

    @Test
    public void parseTypeTest() throws Exception {
        assertEquals(Integer.class, FormulaImpl2.parseType(INTEGER_STRING));
        assertEquals(Integer.class, FormulaImpl2.parseType(INTEGER_STRING_2));
        assertEquals(Integer.class, FormulaImpl2.parseType(NEG_INTEGER_STRING));
        assertEquals(Integer.class, FormulaImpl2.parseType(NEG_INTEGER_STRING_2));
        assertEquals(ReferenceCell.class, FormulaImpl2.parseType(REFERENCE));
        assertEquals(ReferenceCell.class, FormulaImpl2.parseType(REFERENCE_2));
        assertEquals(String.class, FormulaImpl2.parseType(STRING));
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongStringTypeTest() {
        FormulaImpl2.parseType(WRONG_STRING);
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongStringNullTypeTest() {
        FormulaImpl2.parseType(STRIN_NULL);
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongIntegerTypeTest() {
        FormulaImpl2.parseType(WRONG_INTEGER);
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongReferenceTypeTest() {
        FormulaImpl2.parseType(WRONG_REFERENCE);
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongExpressionTest() {
        FormulaImpl2.parseType(WRONG_EXPRESSION);
    }

    @Test (expected = FormatErrorException.class)
    public void parseWrongExpressionTest2() {
        FormulaImpl2.parseType(WRONG_EXPRESSION_2);
    }

    @Test
    public void findPatternTest() {

        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), STRIN_NULL));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), NEG_REFERENCE_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), NEG_REFERENCE));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), REFERENCE));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), REFERENCE_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), INTEGER_STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), INTEGER_STRING_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), NEG_INTEGER_STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), NEG_INTEGER_STRING_2));

        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), STRIN_NULL));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), NEG_REFERENCE_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), NEG_REFERENCE));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), REFERENCE));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), REFERENCE_2));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), INTEGER_STRING));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), INTEGER_STRING_2));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), NEG_INTEGER_STRING));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), NEG_INTEGER_STRING_2));

        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), STRIN_NULL));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), NEG_REFERENCE_2));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), NEG_REFERENCE));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), REFERENCE));
        assertTrue(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), REFERENCE_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), INTEGER_STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), INTEGER_STRING_2));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), NEG_INTEGER_STRING));
        assertFalse(FormulaImpl2.findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), NEG_INTEGER_STRING_2));
    }

    @Test
    public void parseObjectFromStringTest() {
        try
        {
            assertEquals(STRING, FormulaImpl2.parseObjectFromString(new DataWrapper(String.class, STRING)));
            assertEquals(String.class, FormulaImpl2.parseObjectFromString(new DataWrapper(String.class, STRING)).getClass());
            assertEquals(Integer.parseInt(INTEGER_STRING), FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING)));
            assertEquals(Integer.class, FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING)).getClass());
            assertEquals(Integer.parseInt(INTEGER_STRING_2), FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING_2)));
            assertEquals(Integer.class, FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING_2)).getClass());
            assertEquals(Integer.parseInt(NEG_INTEGER_STRING), FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING)));
            assertEquals(Integer.class, FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING)).getClass());
            assertEquals(Integer.parseInt(NEG_INTEGER_STRING_2), FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING_2)));
            assertEquals(Integer.class, FormulaImpl2.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING_2)).getClass());
            assertEquals(new ReferenceCell(REFERENCE), FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE)));
            assertEquals(ReferenceCell.class, FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE)).getClass());
            assertEquals(new ReferenceCell(REFERENCE_2), FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE_2)));
            assertEquals(ReferenceCell.class, FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE_2)).getClass());
            assertEquals(new ReferenceCell(NEG_REFERENCE), FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE)));
            assertEquals(ReferenceCell.class, FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE)).getClass());
            assertEquals(new ReferenceCell(NEG_REFERENCE_2), FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE_2)));
            assertEquals(ReferenceCell.class, FormulaImpl2.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE_2)).getClass());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parseExpressionTest() {
//        =5+3-4
        List<DataWrapper> result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "5"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(Integer.class, "3"));
        result.add(new DataWrapper(Operation.class, Operation.SUBSTRACTION.toString()));
        result.add(new DataWrapper(Integer.class, "4"));
        List<DataWrapper> resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_1);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < resultOfParsing.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-A1+3-A1+B11/2*B1
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "-A1"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(Integer.class, "3"));
        result.add(new DataWrapper(Operation.class, Operation.SUBSTRACTION.toString()));
        result.add(new DataWrapper(ReferenceCell.class, "A1"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(ReferenceCell.class, "B11"));
        result.add(new DataWrapper(Operation.class, Operation.DIVISION.toString()));
        result.add(new DataWrapper(Integer.class, "2"));
        result.add(new DataWrapper(Operation.class, Operation.MULTIPLICATION.toString()));
        result.add(new DataWrapper(ReferenceCell.class, "B1"));
        resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_2);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =3
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "3"));
        resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_6);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-3
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "-3"));
        resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_3);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =A1
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "A1"));
        resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_5);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-A1
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "-A1"));
        resultOfParsing = FormulaImpl2.parseExpression(EXPRESSION_4);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }
    }

    @Test
    public void calculateExpressionTest() {

//            =5+3-4
        assertEquals(new DataWrapper(Integer.class, "4"), FormulaImpl2.calculate(data, EXPRESSION_1));

//            =-A1+3-A1+B11/2*B1
        assertEquals(new DataWrapper(Integer.class, "12"), FormulaImpl2.calculate(data, EXPRESSION_2));

//            =-3
        assertEquals(new DataWrapper(Integer.class, "-3"), FormulaImpl2.calculate(data, EXPRESSION_3));

//            =-A1
        assertEquals(new DataWrapper(Integer.class, "3"), FormulaImpl2.calculate(data, EXPRESSION_4));

//            =A1
        assertEquals(new DataWrapper(Integer.class, "-3"), FormulaImpl2.calculate(data, EXPRESSION_5));

//            =3
        assertEquals(new DataWrapper(Integer.class, "3"), FormulaImpl2.calculate(data, EXPRESSION_6));
    }

    @Test (expected = FormatErrorException.class)
    public void calculateExpressionErrorTermTest() {
        FormulaImpl2.calculate(data, WRONG_EXPRESSION);
    }
    @Test (expected = FormatErrorException.class)
         public void calculateExpressionErrorOperatorInTheEndTest() {
        FormulaImpl2.calculate(data, WRONG_EXPRESSION_2);
    }

    @Test (expected = FormatErrorException.class)
    public void calculateExpressionErrorDivisionByZeroTest() {
        FormulaImpl2.calculate(data, WRONG_EXPRESSION_3);
    }

    @Test
    public void parseDependenciesTest() {
        Set<CellId> dependencies;
//            =5+3-4
        dependencies = new TreeSet();
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_1));

//            =-A1+3-A1+B11/2*B1
        dependencies = new TreeSet();
        dependencies.add(new CellId("A1"));
        dependencies.add(new CellId("B1"));
        dependencies.add(new CellId("B11"));
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_2));

//            =-3
        dependencies = new TreeSet();
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_3));

//            =-A1
        dependencies = new TreeSet();
        dependencies.add(new CellId("A1"));
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_4));

//            =A1
        dependencies = new TreeSet();
        dependencies.add(new CellId("a1"));
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_5));

//            =3
        dependencies = new TreeSet();
        assertEquals(dependencies, FormulaImpl2.parseDependencies(EXPRESSION_6));
    }

}
