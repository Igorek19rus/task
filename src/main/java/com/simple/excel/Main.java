package com.simple.excel;

public final class Main {

    private Main () {
    };

    final public static String TABLE_DATA1 = "3\t5\n"
            + "=1+2\t=D1/2\t2\t3\t4\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t7\t=8\t9";
    final public static String TABLE_DATA2 = "3\t5\n"
            + "8\t=1-A1\t'rt\t=E1\t=B2\n"
            + "5\t=A1+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t=A1\t=C3\t-9";
    final public static String TABLE_DATA3 = "3\t5\n"
            + "= B2\t1\t2\t=B1\t4\n"
            + "5\t=C2 + D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9";

    final public static String TABLE_DATA4 = "3\t3\n"
            + "=A2\t4\t3\n"
            + "5\t6\t7\t\n"
            + "=2*A1-3\t7\t6\n";

    final public static String TABLE_DATA5 = "1\t3\n"
            + "'11\t1\t=A1+B1\n";

    final public static String TABLE_DATA6 = "3\t4\n"
            + "12\t=C2\t3\t'Sample\n"
            + "=A1+B1*C1/5\t=A2*B1\t=B3-C3\t'Spread\n"
            + "'Test\t=4-3\t5\t'Sheet\n";
    final public static String SIMPLE_NULL_TABLE = "3\t5\n"
            + " \t \t \t \t \n"
            + " \t \t \t \t \n"
            + " \t \t \t ";

    public static void main(final String[] args) {
        String tableString;
        Table table;

        tableString = TABLE_DATA6;

        table = new Table(tableString);
        table.buildChildrenDependencyTrees();
        table.buildParentDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();
    }
}
