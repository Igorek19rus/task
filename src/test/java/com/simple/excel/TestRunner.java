package com.simple.excel;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
/**
 * Created by igor on 27.02.16.
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestFormulaImpl.class, CellTest.class, TableTest.class, CellIdTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println("All test was successful : " + result.wasSuccessful());
    }
}