package test;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private Cell[][] cells;

    public Table(int rows, int cols) {
        cells = new Cell[rows][cols];
    }

    public void populateTable(String[][] values) {
        for (int i = 0; i < values.length; i++) {
            String[] valuesRow = values[i];
            for (int j = 0; j < valuesRow.length; j++) {
                String value = valuesRow[j];
                cells[i][j] = new Cell(i, j, value);
            }
        }
    }

    public void buildChildrenDependenciesTree() {
        for (Cell[] cellsRow : cells) {
            for (Cell cell : cellsRow) {
                if (cell.getType() == Cell.Type.EXPRESSION) {
                    Expression expression = cell.getExpression();
                    expression.parseDependencies(cells);
                }
            }
        }
    }

    /**
     * Build parent dependency trees. Parent dependency of the current cell is a cell collection which reference to the current cell.
     */
    public void buildParentDependenciesTree() {
        final int initialDeep = 1;
        for (int i = 0; i < cells.length; i++) {
            Cell[] cellsRow = cells[i];
            for (int j = 0; j < cellsRow.length; j++) {
                Cell cell = cellsRow[j];
                int[][] visited = new int[cells.length][cellsRow.length];
                try {
                    analyse(initialDeep, visited, i, j);
                } catch (Exception e) {
                }
            }
        }
    }

    private void analyse(final int deep, final int visited[][], final int i, final int j) throws Exception {
        //TODO: do smth with the 2d array.
        visited[i][j] = deep;
        Cell cellIJ = cells[i][j];
        if (cellIJ.getType().equals(Cell.Type.EXPRESSION)) {
            for (Cell childrenCell : cellIJ.getChildrenCellDependencies()) {
                childrenCell.getParentCellDependencies().add(cellIJ);
                childrenCell.getParentCellDependencies().addAll(cellIJ.getParentCellDependencies());
                int deepValue = visited[childrenCell.getRow()][childrenCell.getCol()];
                // If the current dependency's deep is less then the child dependencies deep it means that current cell links to to parent cell in the tree, so make cycle dependencies.
                // The deep 0 means cell is not analyzed yet, so it's a not a cycle dependencies.
                if (deepValue < deep && deepValue != 0) {
                    throw new Exception("Detected cycle dependencies");
                } else {
                    analyse(deep + 1, visited, childrenCell.getRow(), childrenCell.getCol());
                }
            }
        }
    }

    public void calculationTable() {
        Map<Cell, String> childDependencyValues = new HashMap();
        for (int i = 0; i < cells.length; i++) {
            Cell[] cellsRow = cells[i];
            for (int j = 0; j < cellsRow.length; j++) {
                Cell cell = cellsRow[j];
                if (cell.getType().equals(Cell.Type.EXPRESSION)) {
                    calculateCell(i, j, childDependencyValues);
                }
            }
        }
    }

    private String calculateCell(final int i, final int j, final Map<Cell, String> childDependencyValues)
    {
        Cell currentCell = cells[i][j];
        if(currentCell.getType().equals(Cell.Type.EXPRESSION))
        {

            for(Cell childrenCell : currentCell.getChildrenCellDependencies())
            {
                if(childDependencyValues.get(childrenCell) == null)
                {
                    childDependencyValues.put(childrenCell, calculateCell(childrenCell.getRow(), childrenCell.getCol(), childDependencyValues));
                }
            }
            currentCell.calculateValue(childDependencyValues);
            childDependencyValues.put(currentCell, currentCell.getResultValue());
            return currentCell.getResultValue();
        }
        else
        {
            return currentCell.getResultValue();
        }
    }

    public String printTable() {
        StringBuilder builder = new StringBuilder();
        for (Cell[] cellsRow : cells) {
            for (Cell cell : cellsRow) {
                builder.append(cell.getResultValue());
                builder.append("\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
