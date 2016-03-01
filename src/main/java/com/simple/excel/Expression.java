package com.simple.excel;

import java.util.Map;
import java.util.Set;

public interface Expression {
    String getExpression();

    DataWrapper getCalculated();

    void calculate(final Map<String, String> data);

    Set<Cell.CellId> parseDependencies();
}
