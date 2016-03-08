package com.simple.excel;

import java.util.List;

/**
 * Parser of the expression.
 */
public interface Parser {
    /**
     * Parse type of the term or operation.
     *
     * @param sIn string to parse.
     * @return class of possible term or operation.
     */
    Class parseType(final String sIn);

    /**
     * Parse expression and split it by operations.
     *
     * @param expression expression.
     * @return expression split by operations.
     */
    List<DataWrapper> parseExpression(final String expression);

    /**
     * Test whether pattern expression was found in the string.
     *
     * @param pattern regex expression.
     * @param sIn     input string.
     * @return true if it has been finded, false otherwise.
     */
    boolean findPattern(final String pattern, final String sIn);
}
