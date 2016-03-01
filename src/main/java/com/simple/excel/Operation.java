package com.simple.excel;

public enum Operation {
    SUBSTRACTION('-'),
    ADDITION('+'),
    MULTIPLICATION('*'),
    DIVISION('/');

    private char operation;

    Operation(final char operation) {
        this.operation = operation;
    }

    public char getOperation() {
        return operation;
    }

    public static Operation parseOperation(final char op) {
        switch (op) {
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
    public String toString() {
        return String.valueOf(this.getOperation());
    }

    /**
     * Returns priority operation.
     *
     * @param op char of operation.
     * @return byte operation priority.
     */
    public static byte opPrior(final char op) {
        return 1;
    }

    public static byte opPrior(final String op) {
        return 1;
    }
}
