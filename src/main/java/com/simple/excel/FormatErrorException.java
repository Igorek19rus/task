package com.simple.excel;

/**
 * Created by igor on 27.02.16.
 */
public class FormatErrorException extends RuntimeException
{
    public FormatErrorException() {
        super();
    }
    public FormatErrorException(String s) {
        super(s);
    }
    public FormatErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public FormatErrorException(Throwable throwable) {
        super(throwable);
    }
}