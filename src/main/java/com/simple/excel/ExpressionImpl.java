package com.simple.excel;

import java.util.*;

import static com.simple.excel.Operation.opPrior;
import static com.simple.excel.Operation.parseOperation;

public class ExpressionImpl implements Expression {

    final private String expression;

    private DataWrapper calculated = null;

    final private Parser parser;

    public ExpressionImpl(final String expression, final Parser parser) {
        this.expression = expression;
        this.parser = parser;
    }

    public DataWrapper getCalculated() {
        return calculated;
    }

    public String getExpression() {
        return expression;
    }

    /**
     * Get reverse polish notation of the expression.
     *
     * @param rightSideInSplit string expression.
     * @return reverse polish notation.
     * @throws Exception error by parsing expression.
     */
    private List<DataWrapper> getRPN(final List<DataWrapper> rightSideInSplit) {
        if (rightSideInSplit.get(rightSideInSplit.size() - 1).getClazz().equals(Operation.class)) {
            throw new FormatErrorException("Error expression. Check the expression.");
        }

        List<DataWrapper> rpnListInSplit = new ArrayList();
        Stack<DataWrapper> sbStack = new Stack();
        DataWrapper cTmp;
        for (int i = 0; i < rightSideInSplit.size(); i++) {
            DataWrapper exprTerm = rightSideInSplit.get(i);

            if (exprTerm.getClazz().equals(Operation.class)) {
                while (sbStack.size() > 0) {
                    cTmp = sbStack.peek();
                    if (cTmp.getClazz().equals(Operation.class) && opPrior(cTmp.getStringValue()) <= opPrior(cTmp.getStringValue())) {
                        rpnListInSplit.add(sbStack.pop());
                    }
                }
                sbStack.push(exprTerm);
            } else {
                rpnListInSplit.add(exprTerm);
            }
        }

        // Add stack's operators to the reverse polish notation out.
        while (sbStack.size() > 0) {
            rpnListInSplit.add(sbStack.pop());
        }

        return rpnListInSplit;
    }

    public void calculate(final Map<String, String> data) {
        List<DataWrapper> rightSideInSplit = getRPN(parser.parseExpression(expression));
        DataWrapper dAWrapper = null, dBWrapper = null;
        DataWrapper sTmp;
        Deque<DataWrapper> stack = new ArrayDeque();
        Iterator<DataWrapper> it = rightSideInSplit.iterator();

        while (it.hasNext()) {
            sTmp = it.next();
            if (sTmp.getClazz().equals(Operation.class)) {
                if (stack.size() < 2) {
                    throw new FormatErrorException("Incorrect amount of data on the stack for the operation " + sTmp);
                }
                dBWrapper = stack.pop();
                dAWrapper = stack.pop();
                if (!Number.class.isAssignableFrom(dAWrapper.getClazz()) || !Number.class.isAssignableFrom(dBWrapper.getClazz())) {
                    throw new FormatErrorException("Unsupported operation for types " + dAWrapper.getClazz() + " and " + dBWrapper.getClazz());
                }
                Double dA;
                Double dB;
                try {
                    dA = (Double) parseObjectFromString(dAWrapper);
                    dB = (Double) parseObjectFromString(dBWrapper);
                } catch (Exception ex) {
                    throw new FormatErrorException("Parse term exception", ex);
                }
                Operation op = parseOperation(sTmp.getStringValue().charAt(0));
                switch (op) {
                    case ADDITION:
                        dA = dA + dB;
                        break;
                    case SUBTRACTION:
                        dA = dA - dB;
                        break;
                    case DIVISION:
                        if (dB == 0) {
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
            } else {
                Double dAInteger;
                if (sTmp.getClazz().equals(ReferenceCell.class)) {
                    dAInteger = sTmp.getStringValue().charAt(0) == Operation.SUBTRACTION.getOperation() ?
                            -1 * Double.parseDouble(data.get(sTmp.getStringValue().substring(1))) : Double.parseDouble(data.get(sTmp.getStringValue()));
                } else {
                    dAInteger = Double.parseDouble(sTmp.getStringValue());
                }

                stack.push(new DataWrapper(Double.class, Cell.formatDouble(dAInteger)));
            }
        }

        calculated = stack.pop();
    }

    public Set<CellId> parseDependencies() {
        Set<CellId> directDependencies = new HashSet();
        if (expression.isEmpty()) {
            return directDependencies;
        }
        List<DataWrapper> terms = parser.parseExpression(expression);
        for (DataWrapper term : terms) {
            if (term.getClazz().equals(ReferenceCell.class)) {
                directDependencies.add(new CellId(term.getStringValue()));
            }
        }
        return directDependencies;
    }

    private <T> T parseObjectFromString(final DataWrapper<T> parseInfo) throws Exception {
        return (T) parseInfo.getClazz().getConstructor(new Class[]{String.class}).newInstance(parseInfo.getStringValue());
    }
}
