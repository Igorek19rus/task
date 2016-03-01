package com.simple.excel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.simple.excel.Operation.opPrior;
import static com.simple.excel.Operation.parseOperation;

public class ExpressionImpl implements Expression
{
    final static String REFERENCE_PATTERN = "^-?[A-Za-z][0-9]+$";
    final static String INTEGER_PATTERN = "^-?[0-9]+$";
    final static String STRING_PATTERN = "^'\\w+$";

    final private String expression;

    private DataWrapper calculated = null;

    public ExpressionImpl(final String expression)
    {
        this.expression = expression;
    }

    public DataWrapper getCalculated()
    {
        return calculated;
    }

    public String getExpression()
    {
        return expression;
    }

    /**
     * Get reverse polish notation of the expression.
     *
     * @param rightSideInSplit string expression.
     * @return reverse polish notation.
     * @throws Exception error by parsing expression.
     */
    private List<DataWrapper> getRPN(final List<DataWrapper> rightSideInSplit)
    {
        if(rightSideInSplit.get(rightSideInSplit.size() - 1).getClazz().equals(Operation.class))
        {
            throw new FormatErrorException("Error expression. Check the expression.");
        }

        List<DataWrapper> rpnListInSplit = new ArrayList();
        Stack<DataWrapper> sbStack = new Stack();
        DataWrapper cTmp;
        for(int i = 0; i < rightSideInSplit.size(); i++)
        {
            DataWrapper exprTerm = rightSideInSplit.get(i);

            if(exprTerm.getClazz().equals(Operation.class))
            {
                while(sbStack.size() > 0)
                {
                    cTmp = sbStack.peek();
                    if(cTmp.getClazz().equals(Operation.class) && opPrior(cTmp.getStringValue()) <= opPrior(cTmp.getStringValue()))
                    {
                        rpnListInSplit.add(sbStack.pop());
                    }
                }
                sbStack.push(exprTerm);
            }
            else
            {
                rpnListInSplit.add(exprTerm);
            }
        }

        // Add stack's operators to the reverse polish notation out.
        while(sbStack.size() > 0)
        {
            rpnListInSplit.add(sbStack.pop());
        }

        return rpnListInSplit;
    }

    /**
     * Calculate expression.
     *
     * @param data map of CellId-value dependencies.
     * @return expression's result.
     * @throws Exception error by calculation.
     */
    public void calculate(final Map<String, String> data)
    {
        List<DataWrapper> rightSideInSplit = getRPN(Parser.parseExpression(expression));
        DataWrapper dAWrapper = null, dBWrapper = null;
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
                dBWrapper = stack.pop();
                dAWrapper = stack.pop();
                if (!Number.class.isAssignableFrom(dAWrapper.getClazz()) || !Number.class.isAssignableFrom(dBWrapper.getClazz()))
                {
                    throw new FormatErrorException("Unsupported operation for types " + dAWrapper.getClazz() + " and " + dBWrapper.getClazz());
                }
                Double dA;
                Double dB;
                try
                {
                    dA = (Double) parseObjectFromString(dAWrapper);
                    dB = (Double) parseObjectFromString(dBWrapper);
                }
                catch(Exception ex)
                {
                    throw new FormatErrorException("Parse term exception", ex);
                }
                Operation op = parseOperation(sTmp.getStringValue().charAt(0));
                switch(op)
                {
                    case ADDITION:
                        dA = dA + dB;
                        break;
                    case SUBSTRACTION:
                        dA = dA - dB;
                        break;
                    case DIVISION:
                        if(dB == 0)
                        {
                            throw new FormatErrorException("Division by 0.");
                        }
                        dA = dA / dB;
                        break;
                    case MULTIPLICATION:
                        dA = dA * dB;
                        break;
                    default:
                        throw new FormatErrorException("Unsupported_operation " + sTmp);
                }
                stack.push(new DataWrapper(Double.class, Cell.formatDouble(dA)));
            }
            else
            {
                Double dAInteger;
                if(sTmp.getClazz().equals(ReferenceCell.class))
                {
                    dAInteger = sTmp.getStringValue().charAt(0) == Operation.SUBSTRACTION.getOperation() ?
                            -1 * Double.parseDouble(data.get(sTmp.getStringValue().substring(1))) : Double.parseDouble(data.get(sTmp.getStringValue()));
                }
                else
                {
                    dAInteger = Double.parseDouble(sTmp.getStringValue());
                }

                stack.push(new DataWrapper(Double.class, Cell.formatDouble(dAInteger)));
            }
        }

        calculated = stack.pop();
    }

    public Set<Cell.CellId> parseDependencies()
    {
        Set<Cell.CellId> directDependencies = new TreeSet();
        if(expression.isEmpty())
        {
            return directDependencies;
        }
        List<DataWrapper> terms = Parser.parseExpression(expression);
        for(DataWrapper term : terms)
        {
            if(term.getClazz().equals(ReferenceCell.class))
            {
                directDependencies.add(new Cell.CellId(term.getStringValue()));
            }
        }
        return directDependencies;
    }

    public static <T> T parseObjectFromString(final DataWrapper<T> parseInfo) throws Exception
    {
        return (T) parseInfo.getClazz().getConstructor(new Class[]{String.class}).newInstance(parseInfo.getStringValue());
    }



    public static class Parser {
        public static List<DataWrapper> parseExpression(final String expression)
        {
            if(expression.charAt(0) != '=')
            {
                throw new FormatErrorException("Format error expression");
            }
            String expr = expression.substring(1);
            //        String operationPattern = "[-|+|*|/]";
            StringBuilder operationPattern = new StringBuilder();
            operationPattern.append('[');
            for(Operation op : Operation.class.getEnumConstants())
            {
                operationPattern.append(op.getOperation());
                operationPattern.append('|');
            }
            operationPattern.replace(operationPattern.length()-1, operationPattern.length(), "]");
            Pattern p = Pattern.compile(operationPattern.toString());
            Matcher m = p.matcher(expr);
            List list = new ArrayList();
            int start = 0;
            while(m.find())
            {
                if(m.start() == 0 && expr.charAt(0) == '-')
                {
                    if(!m.find())
                    {
                        String term = expr.substring(start);
                        Class clazz = parseType(term);
                        list.add(new DataWrapper(clazz, term));
                        return list;
                    }
                    String term = expr.substring(start, m.start());
                    Class clazz = parseType(term);
                    if(clazz == ReferenceCell.class || clazz.asSubclass(Number.class).equals(clazz))
                    {
                        list.add(new DataWrapper(clazz, term));
                        String op = expr.substring(m.start(), m.end());
                        list.add(new DataWrapper(Operation.class, op));
                        start = m.end();
                    }
                }
                else
                {
                    String term = expr.substring(start, m.start());
                    Class clazz = parseType(term);
                    list.add(new DataWrapper(clazz, term));
                    start = m.end();
                    String op = expr.substring(m.start(), m.end());
                    list.add(new DataWrapper(Operation.class, op));
                }
            }
            String lastTerm = expr.substring(start);
            Class clazz = parseType(lastTerm);
            list.add(new DataWrapper(clazz, lastTerm));
            return list;
        }

        public static Class parseType(final String sIn)
        {
            String sInTrim = sIn.trim();
            if(findPattern(STRING_PATTERN, sInTrim))
            {
                return String.class;
            }
            else if(findPattern(INTEGER_PATTERN, sInTrim))
            {
                return Double.class;
            }
            else if(findPattern(REFERENCE_PATTERN, sInTrim))
            {
                return ReferenceCell.class;
            }

            throw new FormatErrorException("Unrecognized type.");
        }

        public static boolean findPattern(final String pattern, final String sIn)
        {
            if(pattern == null || pattern.isEmpty() || sIn == null || sIn.isEmpty())
            {
                return false;
            }
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(sIn);

            return m.find();
        }
    }
}
