package com.simple.excel;

public enum ErrorMessage
{
    NEGATIVE_VALUE("#negative_value"),
    CYCLE_DEPENDENCIES("#cycle_dependencies"),
    FORMAT_ERROR("#format_error"),
    NO_ERROR("");

    private String error;

    ErrorMessage(final String error)
    {
        this.error = error;
    }

    public String getError()
    {
        return error;
    }
}
