package com.simple.excel;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private DynamicMatrix matrix;

    public Table() {
    }

    public Table(DynamicMatrix matrix) {
        this.matrix = matrix;
    }

    public DynamicMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(DynamicMatrix matrix)
    {
        this.matrix = matrix;
    }

    public void initTable(final String data) {
        String rowPattern = "\n";
        String cellPattern = "\t";

        String[] rows = data.split(rowPattern);
        String[] dimensions = rows[0].split(cellPattern);
        int rowSize = Integer.parseInt(dimensions[0]);
        int colSize = Integer.parseInt(dimensions[1]);

        matrix = new DynamicMatrix(rowSize, colSize);

        for (int k1 = 0; k1<rowSize; k1++) {
            if (k1 >= rows.length-1) {
                continue;
            }
            String[] cellsInRow = rows[k1+1].split(cellPattern);
            for (int k2 = 0; k2<colSize; k2++) {
                if (k2 >= cellsInRow.length) {
                    continue;
                }
                Cell curCell = matrix.getElement(k1, k2);
                curCell.setStringValue(cellsInRow[k2]);
                curCell.initType();

                if (curCell.getExpression() == null) {
                    curCell.calculateValue();
                }
            }
        }
    }

    private void analyse(int deep, int visited[][], int i, int j) throws Exception {
        visited[i][j] = deep;
        Cell cellIJ = matrix.getElement(i, j);
        if (cellIJ.getType().equals(CellType.EXPRESSION)) {
            for (CellId cellId : cellIJ.getChildrenCellDependancies()) {

//                Pair<Integer, Integer> cellIndexes = CellId.cellIdToIndex(cellId.getColumn(),cellId.getRow());
                Pair<Integer, Integer> cellIndexes = CellId.cellIdToIndex(cellId);
                Cell cell = matrix.getElement(cellIndexes.getFirst(), cellIndexes.getSecond());
//                if (cell.getType().equals(CellType.ERROR))
                cell.getParentCellDependancies().add(cellIJ.getCellId());
                cell.getParentCellDependancies().addAll(cellIJ.getParentCellDependancies());
                int deepValue = visited[cellIndexes.getFirst()][cellIndexes.getSecond()];
                if (deepValue < deep && deepValue != 0) {
                    throw new Exception("Cycle dependencies");
                } else {
                    analyse(deep + 1, visited, cellIndexes.getFirst(), cellIndexes.getSecond());
                }
            }
        }
    }

//    private void addParentDependencies(Cell cellToAdd, Cell parentCell) {
//        cellToAdd.getParentCellDependancies().add(parentCell.getCellId());
//    }

    public void buildDependencyTrees() {
        for (int i = 0; i<matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                int[][] visited = new int[matrix.getRowSize()][matrix.getColumnSize()];
                try {
                    analyse(1, visited, i ,j);
                } catch (Exception e) {
//                    matrix.getElement(i, j).setErrorType(ErrorMessage.CYCLE_DEPENDENCIES);
//                    for (Cell.CellId cellId : matrix.getElement(i, j).getChildrenCellDependancies()) {
//                        if (matrix.getElement(cellId).getType().equals(CellType.EXPRESSION)) {
//                            matrix.getElement(cellId).setErrorType(ErrorMessage.CYCLE_DEPENDENCIES);
//                        }
//                    }
//                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkCycleDependencies (int i, int j) {
        return matrix.getElement(i, j).getParentCellDependancies().contains(matrix.getElement(i, j).getCellId());
    }

    public void resolveCycleDependencies() {
        for (int i = 0; i<matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                if (checkCycleDependencies(i, j)){
                    for (CellId cellId : matrix.getElement(i, j).getParentCellDependancies()) {
                        matrix.getElement(cellId).setErrorType(ErrorMessage.CYCLE_DEPENDENCIES);
                    }
                }
//                if (matrix.getElement(i, j).getChildrenCellDependancies().contains(matrix.getElement(i, j).getCellId())) {
//                    for (Cell.CellId cellId : matrix.getElement(i, j).getChildrenCellDependancies()) {
//                        matrix.getElement(cellId).setErrorType(ErrorMessage.CYCLE_DEPENDENCIES);
//                    }
//                }
            }
        }
    }

    private String calculateCell(int i, int j, Map<String, String> dependencies) {
        if (i > matrix.getRowSize() || j > matrix.getColumnSize()) throw new ArrayIndexOutOfBoundsException("Out of bound exception.");
        Cell currentCell = matrix.getElement(i, j);
        if (currentCell.getType().equals(CellType.EXPRESSION))
        {

            for (CellId childrenCellId : currentCell.getChildrenCellDependancies()) {
                final Pair<Integer, Integer> childrenCellIndex = CellId.cellIdToIndex(childrenCellId);
                if (dependencies.get(childrenCellId.toString()) == null) {
                    dependencies.put(childrenCellId.toString(), calculateCell(childrenCellIndex.getFirst(), childrenCellIndex.getSecond(), dependencies));
                }
            }
            try {
                currentCell.getExpression().setCalculated(FormulaImpl2.calculate(dependencies, currentCell.getStringValue()).getStringValue());
                dependencies.put(currentCell.getCellId().toString(), currentCell.getExpression().getCalculated());
                currentCell.calculateValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return currentCell.getValue();
        } else {
            return currentCell.getValue();
        }
    }

    public void calculationTable() {
        Map<String, String> dependencies = new HashMap();
        for (int i = 0; i<matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                Cell cell = matrix.getElement(i, j);
                if (cell.getType().equals(CellType.EXPRESSION)) {
                    calculateCell(i, j, dependencies);
                }
            }
        }
    }


}
