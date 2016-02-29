package com.simple.excel;

public class Main
{

    public static String FORMULA = "=82*A2+3*s2+A2*3";
    public static String TABLE_DATA1 = "3\t5\n"
            + "=B2\t1\t2\t3\t4\n"
            + "5\t=C2+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t7\t=8\t9";
    public static String TABLE_DATA2 = "3\t5\n"
            + "8\t=1-A1\t'rt\t=E1\t=B2\n"
            + "5\t=A1+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t=A1\t=C3\t-9";
    public static String TABLE_DATA3 = "3\t5\n"
            + "= B2\t1\t2\t=B1\t4\n"
            + "5\t=C2 + D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "=A1\t=A3\t7\t=8\t9";

    public static String TABLE_DATA4 = "3\t3\n"
            + "=A2\t4\t3\n"
            + "5\t6\t7\t\n"
            + "=2*A1-3\t7\t6\n";

    public static String TABLE_DATA5 = "1\t2\n"
            + "-11\t4\n";

    public static String TABLE_DATA6 = "3\t4\n"
            + "12\t=C2\t3\t'Sample\n"
            + "=A1+B1*C1/5\t=A2*B1\t=B3-C3\t'Spread\n"
            + "'Test\t=4-3\t5\t'Sheet\n";

    public static void main(String[] args)
    {
        String tableString = TABLE_DATA1;
        System.out.println(tableString);
        System.out.println("=====");
        Table table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();

         tableString = TABLE_DATA2;
        System.out.println(tableString);
        System.out.println("=====");
         table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();

        tableString = TABLE_DATA3;
        System.out.println(tableString);
        System.out.println("=====");
        table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();

        tableString = TABLE_DATA4;
        System.out.println(tableString);
        System.out.println("=====");
        table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();

        tableString = TABLE_DATA5;
        System.out.println(tableString);
        System.out.println("=====");
        table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();

        tableString = TABLE_DATA6;
        System.out.println(tableString);
        System.out.println("=====");
        table = new Table(tableString);
        table.buildDependencyTrees();
        table.resolveCycleDependencies();
        table.calculationTable();
        System.out.println(table.getMatrix().printTable());
        System.out.println();


//        System.out.println(tableString);
//        System.out.println("=====");
//        //TODO: move init table to contructor?
//        table = new Table(tableString);
////        table.initTable(tableString);
//        //TODO: how much time parsing of expressions is called? 1, 2, 3?
//        table.buildDependencyTrees();
//        table.resolveCycleDependencies();
////        System.out.println(table.getMatrix().printStringValueTable());
//        table.calculationTable();
//        System.out.println(table.getMatrix().printTable());
////        table.getMatrix().printTableType();
////        System.out.println(table.getMatrix().printTableIndex());
////        table.getMatrix().showChildrenCellDependancies();
////        table.getMatrix().showParentCellDependancies();
    }
}
