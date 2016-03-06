package com.simple.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserImpl implements Parser
{
    final static String REFERENCE_PATTERN = "^-?[A-Za-z][0-9]+$";
    final static String INTEGER_PATTERN = "^-?[0-9]+$";
    final static String STRING_PATTERN = "^'\\w+$";

    public List<DataWrapper> parseExpression(final String expression) {
        if (expression.charAt(0) != '=') {
            throw new FormatErrorException("Format error expression");
        }
        String expr = expression.substring(1);
        // "[-|+|*|/]";
        StringBuilder operationPattern = new StringBuilder();
        operationPattern.append('[');
        for (Operation op : Operation.class.getEnumConstants()) {
            operationPattern.append(op.getOperation());
            operationPattern.append('|');
        }
        operationPattern.replace(operationPattern.length() - 1, operationPattern.length(), "]");
        Pattern p = Pattern.compile(operationPattern.toString());
        Matcher m = p.matcher(expr);
        List list = new ArrayList();
        int start = 0;
        while (m.find()) {
            if (m.start() == 0 && expr.charAt(0) == '-') {
                if (!m.find()) {
                    String term = expr.substring(start);
                    Class clazz = parseType(term);
                    list.add(new DataWrapper(clazz, term));
                    return list;
                }
                String term = expr.substring(start, m.start());
                Class clazz = parseType(term);
                if (clazz == ReferenceCell.class || clazz.asSubclass(Number.class).equals(clazz)) {
                    list.add(new DataWrapper(clazz, term));
                    String op = expr.substring(m.start(), m.end());
                    list.add(new DataWrapper(Operation.class, op));
                    start = m.end();
                }
            } else {
                String term = expr.substring(start, m.start());
                Class clazz = parseType(term);
                list.add(new DataWrapper(clazz, term));
                start = m.end();
                String op = expr.substring(m.start(), m.end());
                list.add(new DataWrapper(Operation.class, op));
            }
        }
        String lastTerm = expr.substring(start);
        Class clazz = parseType(lastTerm);
        list.add(new DataWrapper(clazz, lastTerm));
        return list;
    }

    public Class parseType(final String sIn) {
        String sInTrim = sIn.trim();
        if (findPattern(STRING_PATTERN, sInTrim)) {
            return String.class;
        } else if (findPattern(INTEGER_PATTERN, sInTrim)) {
            return Integer.class;
        } else if (findPattern(REFERENCE_PATTERN, sInTrim)) {
            return ReferenceCell.class;
        }

        throw new FormatErrorException("Unrecognized type.");
    }

    public boolean findPattern(final String pattern, final String sIn) {
        if (pattern == null || pattern.isEmpty() || sIn == null || sIn.isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(sIn);

        return m.find();
    }
    }
