package test;

public class Main {
    final public static String TABLE_DATA = "3\t5\n"
            + "8\t=1-A1\t'rt\t=E1\t=B2\n"
            + "5\t=A1+D2+E2\t=A3\t=A3+B3\t=B3\n"
            + "6\t=A3\t=A1\t=C3\t-9";

    public static void main(String[] args) {
        String [] rows = TABLE_DATA.split("\n");
        String [] firstRow = rows[0].split("\t");
        int rowsNumber = Integer.parseInt(firstRow[0]);
        int colsNumber = Integer.parseInt(firstRow[1]);

        String [][] tableValues = new String[rowsNumber][colsNumber];
        for (int i = 1; i < rows.length; i++) {
            tableValues[i-1] = rows[i].split("\t");
        }
        Table table = new Table(rowsNumber, colsNumber);
        table.populateTable(tableValues);
        table.buildChildrenDependenciesTree();
        table.buildParentDependenciesTree();
        table.calculationTable();
        System.out.println(table.printTable());
    }
}
