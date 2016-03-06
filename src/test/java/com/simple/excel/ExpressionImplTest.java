package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ExpressionImplTest {
    private static Logger log = LogManager.getLogger(Table.class);

    final String REFERENCE_PATTERN = "^-?[A-Za-z][0-9]+$";
    final String INTEGER_PATTERN = "^-?[0-9]+$";
    final String STRING_PATTERN = "^'\\w+$";

    final private String EXPRESSION_1 = "=-A1+A2/A3*3";
    final private String EXPRESSION_2 = "=A1+3-4";
    final private String EXPRESSION_3 = "=-A44";
    final private String EXPRESSION_4 = "=A44";
    final private String EXPRESSION_5 = "=A5+A2";
    final private String EXPRESSION_6 = "=-A1+3";
    final private String EXPRESSION_7 = "=A5";
    final private String EXPRESSION_8 = "=A5+3";
    final private String EXPRESSION_9 = "=A6+A2";
    final private String EXPRESSION_10 = "=A6+A5";
    final private String EXPRESSION_11 = "=A6";
    final private String EXPRESSION_12 = "=-A6";
    final private String EXPRESSION_13 = "=-A6+A2";
    final private String EXPRESSION_14 = "=-3";
    final private String EXPRESSION_15 = "=3";

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

    private final static Map<CellId, Cell> data = new HashMap();

    static {
        Cell cell1 = new Cell("A1");
        cell1.setOriginalValue("3");
        cell1.initType();
        cell1.calculateValue(data);
        data.put(cell1.getCellId(), cell1);

        Cell cell2 = new Cell("A2");
        cell2.setOriginalValue("1");
        cell2.initType();
        cell2.calculateValue(data);
        data.put(cell2.getCellId(), cell2);

        Cell cell3 = new Cell("A3");
        cell3.setOriginalValue("0");
        cell3.initType();
        cell3.calculateValue(data);
        data.put(cell3.getCellId(), cell3);

        Cell cell4 = new Cell("A44");
        cell4.setOriginalValue("-1");
        cell4.initType();
        cell4.calculateValue(data);
        data.put(cell4.getCellId(), cell4);

        Cell cell5 = new Cell("A5");
        cell5.setOriginalValue("'11");
        cell5.initType();
        cell5.calculateValue(data);
        data.put(cell5.getCellId(), cell5);

        Cell cell6 = new Cell("A6");
        cell6.setOriginalValue("");
        cell6.initType();
        cell6.calculateValue(data);
        data.put(cell6.getCellId(), cell6);
    }

    @Test
    public void parseTypeTest() throws Exception {
        Parser parser = new ParserImpl();
        assertEquals(Integer.class, parser.parseType(INTEGER_STRING));
        assertEquals(Integer.class, parser.parseType(INTEGER_STRING_2));
        assertEquals(Integer.class, parser.parseType(NEG_INTEGER_STRING));
        assertEquals(Integer.class, parser.parseType(NEG_INTEGER_STRING_2));
        assertEquals(ReferenceCell.class, parser.parseType(REFERENCE));
        assertEquals(ReferenceCell.class, parser.parseType(REFERENCE_2));
        assertEquals(String.class, parser.parseType(STRING));
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongStringTypeTest() {
        new ParserImpl().parseType(WRONG_STRING);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongStringNullTypeTest() {
        new ParserImpl().parseType(STRIN_NULL);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongIntegerTypeTest() {
        new ParserImpl().parseType(WRONG_INTEGER);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongReferenceTypeTest() {
        new ParserImpl().parseType(WRONG_REFERENCE);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongExpressionTest() {
        new ParserImpl().parseType(WRONG_EXPRESSION);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongExpressionTest2() {
        new ParserImpl().parseType(WRONG_EXPRESSION_2);
    }

    @Test
    public void findPatternTest() {
        Parser parser = new ParserImpl();
        assertTrue(parser.findPattern(STRING_PATTERN, STRING));
        assertFalse(parser.findPattern(STRING_PATTERN, STRIN_NULL));
        assertFalse(parser.findPattern(STRING_PATTERN, NEG_REFERENCE_2));
        assertFalse(parser.findPattern(STRING_PATTERN, NEG_REFERENCE));
        assertFalse(parser.findPattern(STRING_PATTERN, REFERENCE));
        assertFalse(parser.findPattern(STRING_PATTERN, REFERENCE_2));
        assertFalse(parser.findPattern(STRING_PATTERN, INTEGER_STRING));
        assertFalse(parser.findPattern(STRING_PATTERN, INTEGER_STRING_2));
        assertFalse(parser.findPattern(STRING_PATTERN, NEG_INTEGER_STRING));
        assertFalse(parser.findPattern(STRING_PATTERN, NEG_INTEGER_STRING_2));

        assertFalse(parser.findPattern(INTEGER_PATTERN, STRING));
        assertFalse(parser.findPattern(INTEGER_PATTERN, STRIN_NULL));
        assertFalse(parser.findPattern(INTEGER_PATTERN, NEG_REFERENCE_2));
        assertFalse(parser.findPattern(INTEGER_PATTERN, NEG_REFERENCE));
        assertFalse(parser.findPattern(INTEGER_PATTERN, REFERENCE));
        assertFalse(parser.findPattern(INTEGER_PATTERN, REFERENCE_2));
        assertTrue(parser.findPattern(INTEGER_PATTERN, INTEGER_STRING));
        assertTrue(parser.findPattern(INTEGER_PATTERN, INTEGER_STRING_2));
        assertTrue(parser.findPattern(INTEGER_PATTERN, NEG_INTEGER_STRING));
        assertTrue(parser.findPattern(INTEGER_PATTERN, NEG_INTEGER_STRING_2));

        assertFalse(parser.findPattern(REFERENCE_PATTERN, STRING));
        assertFalse(parser.findPattern(REFERENCE_PATTERN, STRIN_NULL));
        assertTrue(parser.findPattern(REFERENCE_PATTERN, NEG_REFERENCE_2));
        assertTrue(parser.findPattern(REFERENCE_PATTERN, NEG_REFERENCE));
        assertTrue(parser.findPattern(REFERENCE_PATTERN, REFERENCE));
        assertTrue(parser.findPattern(REFERENCE_PATTERN, REFERENCE_2));
        assertFalse(parser.findPattern(REFERENCE_PATTERN, INTEGER_STRING));
        assertFalse(parser.findPattern(REFERENCE_PATTERN, INTEGER_STRING_2));
        assertFalse(parser.findPattern(REFERENCE_PATTERN, NEG_INTEGER_STRING));
        assertFalse(parser.findPattern(REFERENCE_PATTERN, NEG_INTEGER_STRING_2));
    }

    @Test
    public void parseExpressionTest() {
        Parser parser = new ParserImpl();

//        =A1+3-4
        List<DataWrapper> result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "A1"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(Integer.class, "3"));
        result.add(new DataWrapper(Operation.class, Operation.SUBTRACTION.toString()));
        result.add(new DataWrapper(Integer.class, "4"));
        List<DataWrapper> resultOfParsing = parser.parseExpression(EXPRESSION_2);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < resultOfParsing.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        ==-A1+A2/A3*3
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "-A1"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(ReferenceCell.class, "A2"));
        result.add(new DataWrapper(Operation.class, Operation.DIVISION.toString()));
        result.add(new DataWrapper(ReferenceCell.class, "A3"));
        result.add(new DataWrapper(Operation.class, Operation.MULTIPLICATION.toString()));
        result.add(new DataWrapper(Integer.class, "3"));
        resultOfParsing = parser.parseExpression(EXPRESSION_1);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =3
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "3"));
        resultOfParsing = parser.parseExpression(EXPRESSION_15);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-3
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "-3"));
        resultOfParsing = parser.parseExpression(EXPRESSION_14);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =A1
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "A44"));
        resultOfParsing = parser.parseExpression(EXPRESSION_4);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-A1
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "-A44"));
        resultOfParsing = parser.parseExpression(EXPRESSION_3);
        assertEquals(result.size(), resultOfParsing.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }
    }

    @Test
    public void calculateExpressionTest() {
        ExpressionImpl expression;
        Parser parser = new ParserImpl();

//            =A1+3-4
        expression = new ExpressionImpl(EXPRESSION_2, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "2"), expression.getCalculated());

//            =-A1+3
        expression = new ExpressionImpl(EXPRESSION_6, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "0"), expression.getCalculated());

//            =A6+A2
        expression = new ExpressionImpl(EXPRESSION_9, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "1"), expression.getCalculated());

//            =A6
        expression = new ExpressionImpl(EXPRESSION_11, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "0"), expression.getCalculated());

//            =-A6
        expression = new ExpressionImpl(EXPRESSION_12, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "0"), expression.getCalculated());

//            =A6-A2
        expression = new ExpressionImpl(EXPRESSION_13, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "1"), expression.getCalculated());

//            =-3
        expression = new ExpressionImpl(EXPRESSION_14, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "-3"), expression.getCalculated());

//            =3
        expression = new ExpressionImpl(EXPRESSION_15, parser);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "3"), expression.getCalculated());
    }

    @Test(expected = CellOperationException.class)
    public void calculateStringPlusNullTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_10, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = CellOperationException.class)
    public void calculateStringReferenceTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_7, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = CellOperationException.class)
    public void calculateStringOperationTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_8, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateNegativeReferenceTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_3, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = CellOperationException.class)
    public void calculateErrorOperationTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_5, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateNegativeReference2Test() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_4, new ParserImpl());
        expression.calculate(data);
    }


    @Test(expected = CellOperationException.class)
    public void calculateDivideOnZeroTest() {
        ExpressionImpl expression = new ExpressionImpl(EXPRESSION_1, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateExpressionErrorTermTest() {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateExpressionErrorOperatorInTheEndTest() {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION_2, new ParserImpl());
        expression.calculate(data);
    }

    @Test(expected = CellOperationException.class)
    public void calculateExpressionErrorDivisionByZeroTest() {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION_3, new ParserImpl());
        expression.calculate(data);
    }

    @Test
    public void parseDependenciesTest() {
        Set<CellId> dependencies;
        ExpressionImpl expression;

//            =-A1+A2/A3
        dependencies = new HashSet();
        dependencies.add(new CellId("A1"));
        dependencies.add(new CellId("A2"));
        dependencies.add(new CellId("A3"));
        expression = new ExpressionImpl(EXPRESSION_1, new ParserImpl());
        assertEquals(dependencies, expression.parseDependencies());

//            =-A1+3-4
        dependencies = new HashSet();
        expression = new ExpressionImpl(EXPRESSION_2, new ParserImpl());
        dependencies.add(new CellId("A1"));
        assertEquals(dependencies, expression.parseDependencies());

//            =-A44
        dependencies = new HashSet();
        dependencies.add(new CellId("A44"));
        expression = new ExpressionImpl(EXPRESSION_3, new ParserImpl());
        assertEquals(dependencies, expression.parseDependencies());
    }
}
