package com.simple.excel;

/**
 * Created by igor on 27.02.16.
 */
public enum MatcherTypePattern {

    REFERENCE_PATTERN("^-?[A-Za-z][0-9]+$"),
    INTEGER_PATTERN("^-?[0-9]+$"),
    STRING_PATTERN("^'\\w+$");

    private String pattern;

    MatcherTypePattern(final String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
