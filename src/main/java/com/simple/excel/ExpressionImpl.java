package com.simple.excel;

import java.util.*;

import static com.simple.excel.Operation.opPrior;
import static com.simple.excel.Operation.parseOperation;

/**
 * Implementation which uses the reverse polish notation to calculate expression.
 */
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
     * @param rightSideInSplit expression split by operations.
     * @return reverse polish notation.
     * @throws FormatErrorException exception error by parsing expression.
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
                    if (cTmp.getClazz().equals(Operation.class) && opPrior(cTmp.getStringValue().charAt(0)) <= opPrior(cTmp.getStringValue().charAt(0))) {
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

    @SuppressWarnings("PMD.MissingBreakInSwitch")
    public void calculate(final Map<CellId, Cell> data) {
        List<DataWrapper> rightSideInSplit = getRPN(parser.parseExpression(expression));
        DataWrapper dAWrapper, dBWrapper;
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
                Integer dA = Integer.parseInt(dAWrapper.getStringValue());
                Integer dB = Integer.parseInt(dBWrapper.getStringValue());
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
                            throw new CellOperationException("Division by 0.");
                        }
                        dA = dA / dB;
                        break;
                    case MULTIPLICATION:
                        dA = dA * dB;
                        break;
                    default:
                        throw new FormatErrorException("Unsupported_operation " + sTmp);
                }
                stack.push(new DataWrapper(Integer.class, dA.toString()));
            } else {
                Integer dAInteger;
                if (sTmp.getClazz().equals(String.class)) {
                    throw new CellOperationException("Unsupported expression with string type value.");
                } else if (sTmp.getClazz().equals(ReferenceCell.class)) {
                    final boolean unarySubtraction = sTmp.getStringValue().charAt(0) == Operation.SUBTRACTION.getOperation();
                    Cell cell = unarySubtraction ? data.get(new CellId(sTmp.getStringValue().substring(1))) :
                            data.get(new CellId(sTmp.getStringValue()));
                    switch (cell.getType()) {
                        case NULL:
                            dAInteger = 0;
                            break;
                        case ERROR:
                            throw new FormatErrorException("Cell children dependency has error type.");
                        case STRING:
                            throw new CellOperationException("Unsupported expression with string type value.");
                        default:
                            dAInteger = unarySubtraction ? -1 * Integer.parseInt(cell.getResultValue()) :
                                    Integer.parseInt(cell.getResultValue());
                    }
                } else {
                    dAInteger = Integer.parseInt(sTmp.getStringValue());
                }

                stack.push(new DataWrapper(Integer.class, dAInteger.toString()));
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
}
