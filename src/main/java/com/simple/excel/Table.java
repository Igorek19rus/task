package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class Table {

    private static Logger log = LogManager.getLogger(Table.class);

    final private DynamicMatrix matrix;

    final static private int INITIAL_DEEP = 1;

    public Table(final String data) {
        String rowPattern = "\n";
        String cellPattern = "\t";

        String[] rows = data.split(rowPattern);
        String[] dimensions = rows[0].split(cellPattern);
        int rowSize;
        int colSize;
        try {
            rowSize = Integer.parseInt(dimensions[0]);
            colSize = Integer.parseInt(dimensions[1]);
        } catch (RuntimeException ex) {
            log.error("Error table parse. Error dimention parsing.");
            throw new FormatErrorException("Error table parse. Error dimention parsing.", ex);
        }
        if (rows.length - 1 != rowSize) {
            throw new FormatErrorException("Error table parse. Size error exception.");
        }

        matrix = new DynamicMatrix(rowSize, colSize);

        try {
            for (int k1 = 0; k1 < rowSize; k1++) {
                String[] cellsInRow = rows[k1 + 1].split(cellPattern);
                if (cellsInRow.length != colSize) {
                    throw new FormatErrorException("Error table parse. Size error exception");
                }
                for (int k2 = 0; k2 < colSize; k2++) {
                    Cell curCell = matrix.getElement(k1, k2);
                    curCell.setOriginalValue(cellsInRow[k2]);
                    curCell.initType();

                    if (!curCell.getType().equals(Cell.CellType.EXPRESSION)) {
                        curCell.calculateValue(new HashMap<CellId, Cell>());
                    }
                }
            }
        } catch (RuntimeException ex) {
            log.error("Error parse table.");
            throw new FormatErrorException("Error parse table.", ex);
        }
    }

    public DynamicMatrix getMatrix() {
        return matrix;
    }

    /**
     * Analyze cycle dependencies and add parent related cells to the parent dependency collection.
     *
     * @param deep    analyze deep.
     * @param visited collection of the analyzing cells which store the deep.
     * @param i       cell row index.
     * @param j       cell column index.
     * @throws Exception cycle dependencies exception.
     */
    private void analyse(final int deep, final int visited[][], final int i, final int j) throws CycleDependencyException {
        visited[i][j] = deep;
        Cell curCell = matrix.getElement(i, j);
        if (curCell.getType().equals(Cell.CellType.EXPRESSION)) {
            for (Cell cell : curCell.getChildrenCellDependencies()) {
                Pair<Integer, Integer> cellIndexes = CellId.cellIdToIndexes(cell.getCellId());
                cell.getParentCellDependencies().add(curCell);
                cell.getParentCellDependencies().addAll(curCell.getParentCellDependencies());
                int deepValue = visited[cellIndexes.getFirst()][cellIndexes.getSecond()];
                // If the current dependency's deep is less then the child dependencies deep it means that current cell links to to parent cell in the tree, so make cycle dependencies.
                // The deep 0 means cell is not analyzed yet, so it's a not a cycle dependencies.
                if (deepValue < deep && deepValue != 0) {
                    throw new CycleDependencyException("Detected cycle dependencies");
                } else {
                    analyse(deep + 1, visited, cellIndexes.getFirst(), cellIndexes.getSecond());
                }
            }
        }
    }

    /**
     * Build parent dependency trees. Parent dependency of the current cell is a cell collection which reference to the current cell.
     */
    public void buildParentDependencyTrees() {
        for (int i = 0; i < matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                int[][] visited = new int[matrix.getRowSize()][matrix.getColumnSize()];
                try {
                    analyse(INITIAL_DEEP, visited, i, j);
                } catch (CycleDependencyException e) {
                    log.error("Detected cycle dependencies of the cell " + matrix.getElement(i, j).getCellId(), e);
                }
            }
        }
    }

    /**
     * Build children dependency trees. Parent dependency of the current cell is a cell collection which reference to the current cell.
     */
    public void buildChildrenDependencyTrees() {
        for (int i = 0; i < matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                Cell curCell = matrix.getElement(i, j);
                Set<CellId> childrenCellIds = curCell.initChildrenCellIdDependencies();
                for (CellId cellId : childrenCellIds) {
                    curCell.getChildrenCellDependencies().add(matrix.getElement(cellId));
                }
            }
        }
    }

    /**
     * Check the cycle dependencies of the cell.
     *
     * @param i row index.
     * @param j column index.
     * @return cycle dependency cell flag.
     */
    public boolean isCycleDependencies(final int i, final int j) {
        Cell curCell = matrix.getElement(i, j);
        return curCell.getParentCellDependencies().contains(curCell);
    }

    /**
     * Resolve the cycle dependencies problem by setting cycle dependencies cell type.
     */
    public void resolveCycleDependencies() {
        for (int i = 0; i < matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                if (isCycleDependencies(i, j)) {
                    for (Cell cell : matrix.getElement(i, j).getParentCellDependencies()) {
                        cell.setErrorType(Cell.ErrorMessage.CYCLE_DEPENDENCIES);
                    }
                }
            }
        }
    }

    /**
     * Return calculated output cell value.
     *
     * @param i row cell.
     * @param j column cell.
     * @param childDependencyCalculated map of the children (string cell id - calculated string value) information.
     * @return string value.
     */
    private void calculateCell(final int i, final int j, final Map<CellId, Cell> childDependencyCalculated) {
        if (i > matrix.getRowSize() || j > matrix.getColumnSize()) {
            throw new ArrayIndexOutOfBoundsException("Out of bound exception.");
        }
        Cell currentCell = matrix.getElement(i, j);
        if (currentCell.getType().equals(Cell.CellType.EXPRESSION)) {

            for (Cell childrenCell : currentCell.getChildrenCellDependencies()) {
                final Pair<Integer, Integer> childrenCellIndex = CellId.cellIdToIndexes(childrenCell.getCellId());
                if (childDependencyCalculated.get(childrenCell.getCellId()) == null) {
                    calculateCell(childrenCellIndex.getFirst(), childrenCellIndex.getSecond(), childDependencyCalculated);
                    childDependencyCalculated.put(childrenCell.getCellId(), childrenCell);
                }
            }
            currentCell.calculateValue(childDependencyCalculated);
            childDependencyCalculated.put(currentCell.getCellId(), currentCell);
        }
    }

    /**
     * Calculate displayed cell values.
     */
    public void calculationTable() {
        Map<CellId, Cell> childDependencyCalculated = new HashMap();
        for (int i = 0; i < matrix.getRowSize(); i++) {
            for (int j = 0; j < matrix.getColumnSize(); j++) {
                Cell cell = matrix.getElement(i, j);
                if (cell.getType().equals(Cell.CellType.EXPRESSION)) {
                    calculateCell(i, j, childDependencyCalculated);
                }
            }
        }
    }

    public static class DynamicMatrix {
        final private List<List<Cell>> cells;
        final private int rowSize;
        final private int columnSize;

        public DynamicMatrix(final int rowSize, final int columnSize) {
            this.rowSize = rowSize;
            this.columnSize = columnSize;

            cells = new ArrayList(rowSize);
            for (int i = 0; i < rowSize; i++) {
                cells.add(new ArrayList<Cell>());
                for (int j = 0; j < columnSize; j++) {
                    cells.get(i).add(new Cell(i, j));
                }
            }
        }

        public int getRowSize() {
            return rowSize;
        }

        public int getColumnSize() {
            return columnSize;
        }

        /**
         * Get cell of the matrix.
         *
         * @param i row index.
         * @param j column index.
         * @return element of the matrix.
         */
        public Cell getElement(final int i, final int j) {
            return cells.get(i).get(j);
        }

        /**
         * Get cell of the matrix.
         *
         * @param cellId cell id.
         * @returnlement of the matrix.
         */
        public Cell getElement(final CellId cellId) {
            Pair<Integer, Integer> cellIdToIndex = CellId.cellIdToIndexes(cellId);
            return cells.get(cellIdToIndex.getFirst()).get(cellIdToIndex.getSecond());
        }

        public String printTable() {
            StringBuilder builder = new StringBuilder();
            for (List<Cell> cellList : cells) {
                for (Cell cell : cellList) {
                    builder.append(cell.getResultValue());
                    builder.append("\t");
                }
                builder.replace(builder.length() - 1, builder.length(), "\n");
            }
            builder.delete(builder.length() - 1, builder.length());
            return builder.toString();
        }
    }
}
