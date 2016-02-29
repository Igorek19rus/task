package com.simple.excel;

public enum MatcherTypePattern1
{

    REFERENCE_PATTERN("^-?[A-Za-z][0-9]+$"),
    INTEGER_PATTERN("^-?[0-9]+$"),
    STRING_PATTERN("^'\\w+$");

    private String pattern;

    MatcherTypePattern1(final String pattern)
    {
        this.pattern = pattern;
    }

    public String getPattern()
    {
        return pattern;
    }
}
