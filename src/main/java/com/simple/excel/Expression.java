package com.simple.excel;

import java.util.Map;
import java.util.Set;

/**
 * Expression which can parse dependency and calculate the expression value.
 */
public interface Expression {
    String getExpression();

    /**
     * Get calculated value of the expression.
     *
     * @return calculated value.
     */
    DataWrapper getCalculated();

    /**
     * Calculate expression.
     *
     * @param data map of CellId-cell dependencies.
     * @throws FormatErrorException exception error by calculation.
     * @throws CellOperationException exception error by incompatible types to operate.
     */
    void calculate(final Map<CellId, Cell> data);

    /**
     * Parse children dependencies.
     *
     * @return set of depended cell id
     */
    Set<CellId> parseDependencies();
}
