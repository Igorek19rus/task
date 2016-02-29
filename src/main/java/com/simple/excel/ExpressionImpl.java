package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.simple.excel.Operation.opPrior;
import static com.simple.excel.Operation.parseOperation;

public class ExpressionImpl implements Expression
{
    final String REFERENCE_PATTERN = "^-?[A-Za-z][0-9]+$";
    final String INTEGER_PATTERN = "^-?[0-9]+$";
    final String STRING_PATTERN = "^'\\w+$";


    private static Logger log = LogManager.getLogger(ExpressionImpl.class);

    private String expression;

    private DataWrapper calculated = null;

    public ExpressionImpl(String expression)
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
    private List<DataWrapper> getRPN(List<DataWrapper> rightSideInSplit)
    {
        if(rightSideInSplit.get(rightSideInSplit.size() - 1).getClazz().equals(Operation.class))
        {
            throw new FormatErrorException("Error expression. Check the expression.");
        }

        List<DataWrapper> RPNListInSplit = new ArrayList();
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
                    if(cTmp.getClazz().equals(Operation.class) && (opPrior(cTmp.getStringValue()) <= opPrior(cTmp.getStringValue())))
                    {
                        RPNListInSplit.add(sbStack.pop());
                    }
                }
                sbStack.push(exprTerm);
            }
            else
            {
                RPNListInSplit.add(exprTerm);
            }
        }

        // Add stack's operators to the reverse polish notation out.
        while(sbStack.size() > 0)
        {
            RPNListInSplit.add(sbStack.pop());
        }

        return RPNListInSplit;
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
        List<DataWrapper> rightSideInSplit = getRPN(parseExpression());
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
                }
                catch(Exception ex)
                {
                    throw new FormatErrorException("Parse term exception");
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
                            -1 * Integer.parseInt(data.get(sTmp.getStringValue().substring(1))) : Integer.parseInt(data.get(sTmp.getStringValue()));
                }
                else
                {
                    dAInteger = Integer.parseInt(sTmp.getStringValue());
                }

                stack.push(new DataWrapper(Integer.class, dAInteger.toString()));
            }
        }

        calculated = stack.pop();
    }

    public Set<CellId> parseDependencies()
    {
        Set<CellId> directDependencies = new TreeSet();
        if(expression.isEmpty())
        {
            return directDependencies;
        }
        List<DataWrapper> terms = parseExpression();
        for(DataWrapper term : terms)
        {
            if(term.getClazz().equals(ReferenceCell.class))
            {
                directDependencies.add(new CellId(term.getStringValue()));
            }
        }
        return directDependencies;
    }

    public static <T> T parseObjectFromString(DataWrapper<T> parseInfo) throws Exception
    {
        return (T) parseInfo.getClazz().getConstructor(new Class[]{String.class}).newInstance(parseInfo.getStringValue());
    }

    public List<DataWrapper> parseExpression()
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

    public Class parseType(final String sIn)
    {
        String sInTrim = sIn.trim();
        if(findPattern(STRING_PATTERN, sInTrim))
        {
            return String.class;
        }
        else if(findPattern(INTEGER_PATTERN, sInTrim))
        {
            return Integer.class;
        }
        else if(findPattern(REFERENCE_PATTERN, sInTrim))
        {
            return ReferenceCell.class;
        }

        throw new FormatErrorException("Unrecognized type.");
    }

    public boolean findPattern(final String pattern, final String sIn)
    {
        if(pattern == null || pattern.isEmpty() || sIn == null || sIn.isEmpty())
        {
            return false;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(sIn);

        if(m.find())
        {
            return true;
        }
        return false;
    }
}
