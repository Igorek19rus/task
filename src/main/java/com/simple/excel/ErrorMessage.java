package com.simple.excel;

/**
 * Created by igor on 23.02.16.
 */
public enum ErrorMessage {
    NEGATIVE_VALUE ("#negative_value"),
    CYCLE_DEPENDENCIES ("#cycle_dependencies"),
    FORMAT_ERROR ("#format_error"),
    NO_ERROR ("");

    private String error;

    ErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
