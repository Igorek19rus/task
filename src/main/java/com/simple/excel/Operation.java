package com.simple.excel;

/**
 * Created by igor on 25.02.16.
 */
public enum Operation {
    ADDITION('+'),
    SUBSTRACTION('-'),
    MULTIPLICATION('*'),
    DIVISION('/');

    private char operation;

    Operation(final char operation) {
        this.operation = operation;
    }

    public char getOperation()
    {
        return operation;
    }

    public static Operation parseOperation(final char op) {
        switch(op)
        {
            case '+':
                return ADDITION;
            case '-':
                return SUBSTRACTION;
            case '/':
                return DIVISION;
            case '*':
                return MULTIPLICATION;
            default:
                throw new FormatErrorException("Unsupported_operation " + op);
        }
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.getOperation());
    }

    /**
     * Returns priority operation.
     * @param op char of operation.
     * @return byte operation priority.
     */
    public static byte opPrior(char op)
    {
        return 1;
    }

    public static byte opPrior(String op)
    {
        return 1;
    }
}