package com.simple.excel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaImpl{

    private String expression;


    private String calculated;


    public FormulaImpl(String expression) {
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
     * @param sIn string expression.
     * @return reverse polish notation string.
     * @throws Exception error by parsing expression.
     */
    private String getRPN(String sIn) throws Exception
    {
        if (isOp(sIn.charAt(sIn.length() - 1)))
        {
            throw new Exception("Error expression. Check the expression.");
        }

        String rightSide = sIn.substring(1);

        StringBuilder sbStack = new StringBuilder(""), sbOut = new StringBuilder("");
        char cIn, cTmp;
        for (int i = 0; i < rightSide.length(); i++) {
            cIn = rightSide.charAt(i);

            if (isOp(cIn)) {
                if (i == 0) {
                    sbOut.append("0 ");
                }
                while (sbStack.length() > 0) {
                    cTmp = sbStack.substring(sbStack.length()-1).charAt(0);
                    if (isOp(cTmp) && (opPrior(cIn) <= opPrior(cTmp))) {
                        sbOut.append(" ").append(cTmp).append(" ");
                        sbStack.setLength(sbStack.length()-1);
                    } else {
                        sbOut.append(" ");
                        break;
                    }
                }
                sbOut.append(" ");
                sbStack.append(cIn);
            } else if ('(' == cIn) {
                sbStack.append(cIn);
            } else if (')' == cIn) {
                cTmp = sbStack.substring(sbStack.length()-1).charAt(0);
                while ('(' != cTmp) {
                    if (sbStack.length() < 1) {
                        throw new Exception("Error parsing brackets. Check the expression.");
                    }
                    sbOut.append(" ").append(cTmp);
                    sbStack.setLength(sbStack.length()-1);
                    cTmp = sbStack.substring(sbStack.length()-1).charAt(0);
                }
                sbStack.setLength(sbStack.length()-1);
            } else {
                sbOut.append(cIn);
            }
        }

        // Add stack's operators to the sting out.
        while (sbStack.length() > 0) {
            sbOut.append(" ").append(sbStack.substring(sbStack.length()-1));
            sbStack.setLength(sbStack.length()-1);
        }

        return  sbOut.toString();
    }

    /**
     * Calculate expression.
     * @param data map of CellId-value dependencies.
     * @return expression's result.
     * @throws Exception error by calculation.
     */
    public Integer  calculate (Map<String, String> data) throws Exception {
        String RPNString = getRPN(expression);
        int dA = 0, dB = 0;
        String sTmp;
        Deque<Integer> stack = new ArrayDeque<Integer>();
        StringTokenizer st = new StringTokenizer(RPNString);
        while(st.hasMoreTokens()) {
            try {
                sTmp = st.nextToken().trim();
                if (1 == sTmp.length() && isOp(sTmp.charAt(0))) {
                    if (stack.size() < 2) {
                        throw new Exception("Incorrect amount of data on the stack for the operation " + sTmp);
                    }
                    dB = stack.pop();
                    dA = stack.pop();
                    switch (sTmp.charAt(0)) {
                        case '+':
                            dA += dB;
                            break;
                        case '-':
                            dA -= dB;
                            break;
                        case '/':
                            if (dB == 0) throw new Exception("Division by 0.");
                            dA /= dB;
                            break;
                        case '*':
                            dA *= dB;
                            break;
                        default:
                            throw new Exception("Unsupported_operation " + sTmp);
                    }
                    stack.push(dA);
                } else {
                    if (sTmp.length()>1) {
                        dA = Integer.parseInt(data.get(sTmp));
                    } else {
                        dA = Integer.parseInt(sTmp);
                    }

                    stack.push(dA);
                }
            } catch (Exception e) {
                throw new Exception("Unsupported symbol in the expression.");
            }
        }

        if (stack.size() > 1) {
            throw new Exception("Number of terms don't associated to number of operands.");
        }

        return stack.pop();
    }

    private boolean isOp(char c) {
        switch (c) {
            case '-':
            case '+':
            case '*':
            case '/':
                return true;
        }
        return false;
    }

    /**
     * Returns priority operation.
     * @param op char of operation.
     * @return byte operation priority.
     */
    private byte opPrior(char op)
    {
        return 1;
    }

    public Set<CellId> parseDependencies() {
        Set<CellId> directDependencies = new TreeSet();
        if (expression.isEmpty()) return directDependencies;

        String referencePattern = "[A-Za-z][0-9]";
        Pattern p = Pattern.compile(referencePattern);
        Matcher m = p.matcher(expression);
        while (m.find())
        {
            Cell cell = new Cell(Character.getNumericValue(expression.charAt(m.end() - 1)), Character.toUpperCase(expression.charAt(m.start())));
            directDependencies.add(cell.getCellId());
        }
        return directDependencies;
    }

    public static void main(String[] args) {
        String expression1 = "=5+3";
        String expression2 = "=3-A1";
        String expression3 = "=3+B1";
        String expression4 = "=3";
        String expression5 = "=-A1";
        FormulaImpl expression = new FormulaImpl("");

        Map<String, String> data = new HashMap();
        Cell cell1 = new Cell(0, 0);
        Cell cell2 = new Cell(0, 1);
        Cell cell3 = new Cell(0, 2);
        data.put(cell1.getCellId().toString(),"-3");
        data.put(cell2.getCellId().toString(),"-1");
        data.put(cell3.getCellId().toString(),"3");

        try {
            System.out.print(expression1 + "= 8 = ");
            expression.expression = expression1;
            System.out.println(expression.calculate(data));


            System.out.print(expression2 + "= 6 = ");
            expression.expression = expression2;
            System.out.println(expression.calculate(data));

            System.out.print(expression3 + "= 2 = ");
            expression.expression = expression3;
            System.out.println(expression.calculate(data));

            System.out.print(expression4 + "= 3 = ");
            expression.expression = expression4;
            System.out.println(expression.calculate(data));

            System.out.print(expression5 + "= 3 = ");
            expression.expression = expression5;
            System.out.println(expression.calculate(data));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
