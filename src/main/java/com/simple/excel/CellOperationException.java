package com.simple.excel;

public class CellOperationException extends RuntimeException {
    public CellOperationException() {
        super();
    }

    public CellOperationException(final String s) {
        super(s);
    }

    public CellOperationException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public CellOperationException(final Throwable throwable) {
        super(throwable);
    }
}
