package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ExpressionImplTest
{
    private static Logger log = LogManager.getLogger(Table.class);

    final String REFERENCE_PATTERN = "^-?[A-Za-z][0-9]+$";
    final String INTEGER_PATTERN = "^-?[0-9]+$";
    final String STRING_PATTERN = "^'\\w+$";

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
    public void parseTypeTest() throws Exception
    {
        ExpressionImpl expression = new ExpressionImpl("");
        ;
        assertEquals(Integer.class, expression.parseType(INTEGER_STRING));
        assertEquals(Integer.class, expression.parseType(INTEGER_STRING_2));
        assertEquals(Integer.class, expression.parseType(NEG_INTEGER_STRING));
        assertEquals(Integer.class, expression.parseType(NEG_INTEGER_STRING_2));
        assertEquals(ReferenceCell.class, expression.parseType(REFERENCE));
        assertEquals(ReferenceCell.class, expression.parseType(REFERENCE_2));
        assertEquals(String.class, expression.parseType(STRING));
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongStringTypeTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(WRONG_STRING);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongStringNullTypeTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(STRIN_NULL);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongIntegerTypeTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(WRONG_INTEGER);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongReferenceTypeTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(WRONG_REFERENCE);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongExpressionTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(WRONG_EXPRESSION);
    }

    @Test(expected = FormatErrorException.class)
    public void parseWrongExpressionTest2()
    {
        ExpressionImpl expression = new ExpressionImpl("");
        expression.parseType(WRONG_EXPRESSION_2);
    }

    @Test
    public void findPatternTest()
    {
        ExpressionImpl expression = new ExpressionImpl("");

        assertTrue(expression.findPattern(STRING_PATTERN, STRING));
        assertFalse(expression.findPattern(STRING_PATTERN, STRIN_NULL));
        assertFalse(expression.findPattern(STRING_PATTERN, NEG_REFERENCE_2));
        assertFalse(expression.findPattern(STRING_PATTERN, NEG_REFERENCE));
        assertFalse(expression.findPattern(STRING_PATTERN, REFERENCE));
        assertFalse(expression.findPattern(STRING_PATTERN, REFERENCE_2));
        assertFalse(expression.findPattern(STRING_PATTERN, INTEGER_STRING));
        assertFalse(expression.findPattern(STRING_PATTERN, INTEGER_STRING_2));
        assertFalse(expression.findPattern(STRING_PATTERN, NEG_INTEGER_STRING));
        assertFalse(expression.findPattern(STRING_PATTERN, NEG_INTEGER_STRING_2));

        assertFalse(expression.findPattern(INTEGER_PATTERN, STRING));
        assertFalse(expression.findPattern(INTEGER_PATTERN, STRIN_NULL));
        assertFalse(expression.findPattern(INTEGER_PATTERN, NEG_REFERENCE_2));
        assertFalse(expression.findPattern(INTEGER_PATTERN, NEG_REFERENCE));
        assertFalse(expression.findPattern(INTEGER_PATTERN, REFERENCE));
        assertFalse(expression.findPattern(INTEGER_PATTERN, REFERENCE_2));
        assertTrue(expression.findPattern(INTEGER_PATTERN, INTEGER_STRING));
        assertTrue(expression.findPattern(INTEGER_PATTERN, INTEGER_STRING_2));
        assertTrue(expression.findPattern(INTEGER_PATTERN, NEG_INTEGER_STRING));
        assertTrue(expression.findPattern(INTEGER_PATTERN, NEG_INTEGER_STRING_2));

        assertFalse(expression.findPattern(REFERENCE_PATTERN, STRING));
        assertFalse(expression.findPattern(REFERENCE_PATTERN, STRIN_NULL));
        assertTrue(expression.findPattern(REFERENCE_PATTERN, NEG_REFERENCE_2));
        assertTrue(expression.findPattern(REFERENCE_PATTERN, NEG_REFERENCE));
        assertTrue(expression.findPattern(REFERENCE_PATTERN, REFERENCE));
        assertTrue(expression.findPattern(REFERENCE_PATTERN, REFERENCE_2));
        assertFalse(expression.findPattern(REFERENCE_PATTERN, INTEGER_STRING));
        assertFalse(expression.findPattern(REFERENCE_PATTERN, INTEGER_STRING_2));
        assertFalse(expression.findPattern(REFERENCE_PATTERN, NEG_INTEGER_STRING));
        assertFalse(expression.findPattern(REFERENCE_PATTERN, NEG_INTEGER_STRING_2));
    }

    @Test
    public void parseObjectFromStringTest()
    {
        try
        {
            assertEquals(STRING, ExpressionImpl.parseObjectFromString(new DataWrapper(String.class, STRING)));
            assertEquals(String.class, ExpressionImpl.parseObjectFromString(new DataWrapper(String.class, STRING)).getClass());
            assertEquals(Integer.parseInt(INTEGER_STRING), ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING)));
            assertEquals(Integer.class, ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING)).getClass());
            assertEquals(Integer.parseInt(INTEGER_STRING_2), ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING_2)));
            assertEquals(Integer.class, ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, INTEGER_STRING_2)).getClass());
            assertEquals(Integer.parseInt(NEG_INTEGER_STRING), ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING)));
            assertEquals(Integer.class, ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING)).getClass());
            assertEquals(Integer.parseInt(NEG_INTEGER_STRING_2), ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING_2)));
            assertEquals(Integer.class, ExpressionImpl.parseObjectFromString(new DataWrapper(Integer.class, NEG_INTEGER_STRING_2)).getClass());
            assertEquals(new ReferenceCell(REFERENCE), ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE)));
            assertEquals(ReferenceCell.class, ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE)).getClass());
            assertEquals(new ReferenceCell(REFERENCE_2), ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE_2)));
            assertEquals(ReferenceCell.class, ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, REFERENCE_2)).getClass());
            assertEquals(new ReferenceCell(NEG_REFERENCE), ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE)));
            assertEquals(ReferenceCell.class, ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE)).getClass());
            assertEquals(new ReferenceCell(NEG_REFERENCE_2), ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE_2)));
            assertEquals(ReferenceCell.class, ExpressionImpl.parseObjectFromString(new DataWrapper(ReferenceCell.class, NEG_REFERENCE_2)).getClass());
        }
        catch(Exception e)
        {
            log.info("Error create object by parseObjectFromString");
            e.printStackTrace();
        }
    }

    @Test
    public void parseExpressionTest()
    {
        ExpressionImpl expression;

//        =5+3-4
        expression = new ExpressionImpl(EXPRESSION_1);
        List<DataWrapper> result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "5"));
        result.add(new DataWrapper(Operation.class, Operation.ADDITION.toString()));
        result.add(new DataWrapper(Integer.class, "3"));
        result.add(new DataWrapper(Operation.class, Operation.SUBSTRACTION.toString()));
        result.add(new DataWrapper(Integer.class, "4"));
        List<DataWrapper> resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < resultOfParsing.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-A1+3-A1+B11/2*B1
        expression = new ExpressionImpl(EXPRESSION_2);
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
        resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =3
        expression = new ExpressionImpl(EXPRESSION_6);
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "3"));
        resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-3
        expression = new ExpressionImpl(EXPRESSION_3);
        result = new ArrayList();
        result.add(new DataWrapper(Integer.class, "-3"));
        resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =A1
        expression = new ExpressionImpl(EXPRESSION_5);
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "A1"));
        resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }

//        =-A1
        expression = new ExpressionImpl(EXPRESSION_4);
        result = new ArrayList();
        result.add(new DataWrapper(ReferenceCell.class, "-A1"));
        resultOfParsing = expression.parseExpression();
        assertEquals(result.size(), resultOfParsing.size());
        for(int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i), resultOfParsing.get(i));
        }
    }

    @Test
    public void calculateExpressionTest()
    {
        ExpressionImpl expression;

//            =5+3-4
        expression = new ExpressionImpl(EXPRESSION_1);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "4"), expression.getCalculated());

//            =-A1+3-A1+B11/2*B1
        expression = new ExpressionImpl(EXPRESSION_2);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "12"), expression.getCalculated());

//            =-3
        expression = new ExpressionImpl(EXPRESSION_3);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "-3"), expression.getCalculated());

//            =-A1
        expression = new ExpressionImpl(EXPRESSION_4);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "3"), expression.getCalculated());

//            =A1
        expression = new ExpressionImpl(EXPRESSION_5);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "-3"), expression.getCalculated());

//            =3
        expression = new ExpressionImpl(EXPRESSION_6);
        expression.calculate(data);
        assertEquals(new DataWrapper(Integer.class, "3"), expression.getCalculated());
    }

    @Test(expected = FormatErrorException.class)
    public void calculateExpressionErrorTermTest()
    {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION);
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateExpressionErrorOperatorInTheEndTest()
    {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION_2);
        expression.calculate(data);
    }

    @Test(expected = FormatErrorException.class)
    public void calculateExpressionErrorDivisionByZeroTest()
    {
        ExpressionImpl expression = new ExpressionImpl(WRONG_EXPRESSION_3);
        expression.calculate(data);
    }

    @Test
    public void parseDependenciesTest()
    {
        Set<CellId> dependencies;
        ExpressionImpl expression;

//            =5+3-4
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_1);
        assertEquals(dependencies, expression.parseDependencies());

//            =-A1+3-A1+B11/2*B1
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_2);
        dependencies.add(new CellId("A1"));
        dependencies.add(new CellId("B1"));
        dependencies.add(new CellId("B11"));
        assertEquals(dependencies, expression.parseDependencies());

//            =-3
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_3);
        assertEquals(dependencies, expression.parseDependencies());

//            =-A1
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_4);
        dependencies.add(new CellId("A1"));
        assertEquals(dependencies, expression.parseDependencies());

//            =A1
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_5);
        dependencies.add(new CellId("a1"));
        assertEquals(dependencies, expression.parseDependencies());

//            =3
        dependencies = new TreeSet();
        expression = new ExpressionImpl(EXPRESSION_6);
        assertEquals(dependencies, expression.parseDependencies());
    }

}