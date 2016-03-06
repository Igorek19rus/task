package com.simple.excel;

/**
 * Handle cycle dependency exception.
 */
public class CycleDependencyException extends RuntimeException {
    public CycleDependencyException() {
        super();
    }

    public CycleDependencyException(final String s) {
        super(s);
    }

    public CycleDependencyException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public CycleDependencyException(final Throwable throwable) {
        super(throwable);
    }
}
