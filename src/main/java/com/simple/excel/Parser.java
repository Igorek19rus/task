package com.simple.excel;

import java.util.List;

public interface Parser
{
    Class parseType(final String sIn);
    List<DataWrapper> parseExpression(final String expression);
    boolean findPattern(final String pattern, final String sIn);
}
