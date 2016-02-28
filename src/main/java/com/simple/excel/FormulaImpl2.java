package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.simple.excel.Operation.*;

public class FormulaImpl2 {

    private static Logger log = LogManager.getLogger(FormulaImpl2.class);;
    private String expression;

    private String calculated = null;

    private Map<String, String> data = new HashMap();

    public FormulaImpl2(String expression) {
        this.expression = expression;
    }

    public String getCalculated() {
        return calculated;
    }

    public void setCalculated(String calculated) {
        this.calculated = calculated;
    }

    public String getExpression() {
        return expression;
    }

    /**
     * Get reverse polish notation of the expression.
     * @param rightSideInSplit string expression.
     * @return reverse polish notation.
     * @throws Exception error by parsing expression.
     */
    private static List<DataWrapper> getRPN(List<DataWrapper> rightSideInSplit)
    {
        if (rightSideInSplit.get(rightSideInSplit.size() - 1).getClazz().equals(Operation.class))
        {
            throw new FormatErrorException("Error expression. Check the expression.");
        }

        List<DataWrapper> RPNListInSplit = new ArrayList();
        Stack<DataWrapper> sbStack = new Stack();
        DataWrapper cTmp;
        for (int i = 0; i < rightSideInSplit.size(); i++) {
            DataWrapper exprTerm = rightSideInSplit.get(i);

            if (exprTerm.getClazz().equals(Operation.class)) {
                while (sbStack.size() > 0) {
                    cTmp = sbStack.peek();
                    if (cTmp.getClazz().equals(Operation.class) && (opPrior(cTmp.getStringValue()) <= opPrior(cTmp.getStringValue()))) {
                        RPNListInSplit.add(sbStack.pop());
                    }
                }
                sbStack.push(exprTerm);
            } else {
                RPNListInSplit.add(exprTerm);
            }
        }

        // Add stack's operators to the reverse polish notation out.
        while (sbStack.size() > 0) {
            RPNListInSplit.add(sbStack.pop());
        }

        return  RPNListInSplit;
    }

    /**
     * Calculate expression.
     * @param data map of CellId-value dependencies.
     * @return expression's result.
     * @throws Exception error by calculation.
     */
    public static DataWrapper calculate(final Map<String, String> data, final String expression)
    {
        List<DataWrapper> rightSideInSplit = getRPN(parseExpression(expression));
        DataWrapper dA = null, dB = null;
        DataWrapper sTmp;
        Deque<DataWrapper> stack = new ArrayDeque();
        Iterator<DataWrapper> it = rightSideInSplit.iterator();

        while(it.hasNext())
        {
            sTmp = it.next();
            if(sTmp.getClazz().equals(Operation.class))
            {
                if(stack.size() < 2)
                {
                    throw new FormatErrorException("Incorrect amount of data on the stack for the operation " + sTmp);
                }
                dB = stack.pop();
                dA = stack.pop();
                if(!dA.getClazz().equals(Integer.class) || !dB.getClazz().equals(Integer.class))
                {
                    throw new FormatErrorException("Unsupported operation for types " + dA.getClazz() + " and " + dB.getClazz());
                }
                Integer dAInteger;
                Integer dBInteger;
                try
                {
                    dAInteger = (Integer) parseObjectFromString(dA);
                    dBInteger = (Integer) parseObjectFromString(dB);
                } catch(Exception ex) {
                    throw  new FormatErrorException("Parse term exception");
                }
                Operation op = parseOperation(sTmp.getStringValue().charAt(0));
                switch(op)
                {
                    case ADDITION:
                        dAInteger += dBInteger;
                        break;
                    case SUBSTRACTION:
                        dAInteger -= dBInteger;
                        break;
                    case DIVISION:
                        if(dBInteger == 0)
                        {
                            throw new FormatErrorException("Division by 0.");
                        }
                        dAInteger /= dBInteger;
                        break;
                    case MULTIPLICATION:
                        dAInteger *= dBInteger;
                        break;
                    default:
                        throw new FormatErrorException("Unsupported_operation " + sTmp);
                }
                stack.push(new DataWrapper(Integer.class, dAInteger.toString()));
            }
            else
            {
                Integer dAInteger;
                if(sTmp.getClazz().equals(ReferenceCell.class))
                {
                    dAInteger = sTmp.getStringValue().charAt(0) == Operation.SUBSTRACTION.getOperation() ?
                            -1*Integer.parseInt(data.get(sTmp.getStringValue().substring(1))) : Integer.parseInt(data.get(sTmp.getStringValue()));
                }
                else
                {
                    dAInteger = Integer.parseInt(sTmp.getStringValue());
                }

                stack.push(new DataWrapper(Integer.class, dAInteger.toString()));
            }
        }

        return stack.pop();
    }

    public static Set<CellId> parseDependencies(final String expression) {
        Set<CellId> directDependencies = new TreeSet();
        if (expression.isEmpty()) return directDependencies;
        List<DataWrapper> terms = parseExpression(expression);
        for (DataWrapper term : terms) {
            if (term.getClazz().equals(ReferenceCell.class)) {
                directDependencies.add(new CellId(term.getStringValue()));
            }
        }
        return directDependencies;
    }

    public static <T> T parseObjectFromString(DataWrapper<T> parseInfo) throws Exception {
        return (T) parseInfo.getClazz().getConstructor(new Class[]{String.class}).newInstance(parseInfo.getStringValue());
    }

    public static List<DataWrapper> parseExpression(final String expression) {
        if (expression.charAt(0) != '=') throw new FormatErrorException("Format error expression");
        String expr = expression.substring(1);
        StringBuilder st = new StringBuilder();
        st.append('[');
        for (Operation op : Operation.class.getEnumConstants()) {
            st.append(op.getOperation());
            st.append('|');
        }
        st.append(']');
        String operationPattern = "[-||+|*|/]";
        Pattern p = Pattern.compile(operationPattern);
        Matcher m = p.matcher(expr);
        List list = new ArrayList();
        int start = 0;
        while (m.find())
        {
            if (m.start() == 0 && expr.charAt(0) == '-') {
                if (!m.find()) {
                    String term = expr.substring(start).trim();
                    Class clazz = parseType(term);
                    list.add(new DataWrapper(clazz, term));
                    return list;
                }
                String term = expr.substring(start, m.start()).trim();
                Class clazz = parseType(term);
                if (clazz == ReferenceCell.class || clazz.asSubclass(Number.class).equals(clazz)) {
                     list.add(new DataWrapper(clazz, term));
                     String op = expr.substring(m.start(), m.end()).trim();
                     list.add(new DataWrapper(Operation.class, op));
                     start = m.end();
                }
            } else {
                String term = expr.substring(start, m.start()).trim();
                Class clazz = parseType(term);
                list.add(new DataWrapper(clazz, term));
                start = m.end();
                String op = expr.substring(m.start(), m.end()).trim();
                list.add(new DataWrapper(Operation.class, op));
            }
        }
        String lastTerm = expr.substring(start).trim();
        Class clazz = parseType(lastTerm);
        list.add(new DataWrapper(clazz, lastTerm));
        return list;
    }

    public static Class parseType (final String sIn) {
        if (findPattern(MatcherTypePattern.STRING_PATTERN.getPattern(), sIn.trim())) {
            return String.class;
        } else if (findPattern(MatcherTypePattern.INTEGER_PATTERN.getPattern(), sIn.trim())) {
            return Integer.class;
        } else if (findPattern(MatcherTypePattern.REFERENCE_PATTERN.getPattern(), sIn.trim())) {
            return ReferenceCell.class;
        }

        throw new FormatErrorException("Unrecognize type.");
    }

    public static boolean findPattern(final String pattern, final String sIn) {
        if (pattern == null || pattern.isEmpty() || sIn == null || sIn.isEmpty()) return false;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(sIn);

        if (m.find())
        {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

////        Object obj1 = parseObjectFromString("123", Integer.class);
////        System.out.println("Obj: " + obj1.toString() + "; type: " + obj1.getClass().getSimpleName());
////        Double obj2 = parseObjectFromString("1.23", Double.class);
////        System.out.println("Obj: " + obj2.toString() + "; type: " + obj2.getClass().getSimpleName());
////        Object obj3 = parseObjectFromString("str", String.class);
////        System.out.println("Obj: " + obj3.toString() + "; type: " + obj3.getClass().getSimpleName());
////        Object obj4 = parseObjectFromString("yyyy", SimpleDateFormat.class);
////        System.out.println("Obj: " + obj4.toString() + "; type: " + obj4.getClass().getSimpleName());
//
//        String expression1 = "=5+3-1*2";
//        String expression2 = "=3-A1";
//        String expression3 = "=3+A11*D2/2";
//        String expression4 = "=-A1";
//        String expression5 = "=-A1+33-B12*D2/2";
//        Map<String, String> data = new HashMap();
//        Cell cell1 = new Cell(1, 'A');
//        Cell cell2 = new Cell(2, 'A');
//        Cell cell3 = new Cell(1, 'C');
//        Cell cell4 = new Cell(2, 'D');
//        Cell cell5 = new Cell(11, 'A');
////        Cell cell6 = new Cell(0, 2);
//        data.put(cell1.getCellId().toString(),"-3");
//        data.put(cell2.getCellId().toString(),"-1");
//        data.put(cell3.getCellId().toString(),"3");
//        data.put(cell4.getCellId().toString(),"4");
//        data.put(cell5.getCellId().toString(),"5");
//
//
//        String curExpr = expression4;
//        FormulaImpl2 f = new FormulaImpl2(curExpr);
//        List<DataWrapper> list;
//        list = parseExpression(curExpr);
////        System.out.println(Arrays.toString(list.toArray()));
//        System.out.println(curExpr);
//        for (DataWrapper term : list) {
//            if (term.getClazz() != Operation.class) {
//                Object obj = parseObjectFromString(term);
//                System.out.println("Obj: " + obj.toString() + "; type: " + obj.getClass().getSimpleName());
//            } else {
//                System.out.println("Obj: " + term.getStringValue() + "; type: " + term.getClazz().getSimpleName());
//            }
//        }
//        System.out.println("\nRPN expression");
//        List<DataWrapper> rpn = f.getRPN(list);
//
//        for (DataWrapper term : rpn) {
//            if (term.getClazz() != Operation.class) {
//                Object obj = parseObjectFromString(term);
//                System.out.println("Obj: " + obj.toString() + "; type: " + obj.getClass().getSimpleName());
//            } else {
//                System.out.println("Obj: " + term.getStringValue() + "; type: " + term.getClazz().getSimpleName());
//            }
//        }
//        System.out.println(calculate(data, curExpr).getStringValue() + curExpr);
//
////        list = parseExpression(expression2);
////        System.out.println(Arrays.toString(list.toArray()));
////        list = parseExpression(expression3);
////        System.out.println(Arrays.toString(list.toArray()));
////        list = parseExpression(expression4);
////        System.out.println(Arrays.toString(list.toArray()));
////        list = parseExpression(expression5);
////        System.out.println(Arrays.toString(list.toArray()));
////        FormulaImpl2 expression = new FormulaImpl2("");
////
////        Map<String, String> data = new HashMap<>();
////        Cell cell1 = new Cell(0, 0);
////        Cell cell2 = new Cell(0, 1);
////        Cell cell3 = new Cell(0, 2);
////        data.put(cell1.getCellId().toString(),"-3");
////        data.put(cell2.getCellId().toString(),"-1");
////        data.put(cell3.getCellId().toString(),"3");
////
////        try {
////            System.out.print(expression1 + "= 8 = ");
////            expression.expression = expression1;
////            System.out.println(expression.calculate(data));
////
////
////            System.out.print(expression2 + "= 6 = ");
////            expression.expression = expression2;
////            System.out.println(expression.calculate(data));
////
////            System.out.print(expression3 + "= 2 = ");
////            expression.expression = expression3;
////            System.out.println(expression.calculate(data));
////
////            System.out.print(expression4 + "= 3 = ");
////            expression.expression = expression4;
////            System.out.println(expression.calculate(data));
////
////            System.out.print(expression5 + "= 3 = ");
////            expression.expression = expression5;
////            System.out.println(expression.calculate(data));
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
    }
}
