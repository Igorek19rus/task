package com.simple.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Table
{
    private static Logger log = LogManager.getLogger(Table.class);

    private DynamicMatrix matrix;

    private int INITIAL_DEEP = 1;

    public Table(final String data)
    {
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
        if (rows.length-1 != rowSize)
        {
            throw new FormatErrorException("Error table parse. Size error exception.");
        }

        matrix = new DynamicMatrix(rowSize, colSize);

        Map<String, String> nullData = new HashMap<String, String>();
        try {
            for (int k1 = 0; k1 < rowSize; k1++) {
                String[] cellsInRow = rows[k1 + 1].split(cellPattern);
                if (cellsInRow.length != colSize)
                {
                    throw new FormatErrorException("Error table parse. Size error exception");
                }
                for (int k2 = 0; k2 < colSize; k2++) {
                    Cell curCell = matrix.getElement(k1, k2);
                    curCell.setStringValue(cellsInRow[k2]);
                    curCell.initType();

                    if (!curCell.getType().equals(CellType.EXPRESSION)) {
                        curCell.calculateValue(nullData);
                    }
                }
            }
        } catch (RuntimeException ex) {
            log.error("Error parse table.");
            throw new FormatErrorException("Error parse table.", ex);
        }
    }

    public Table(DynamicMatrix matrix)
    {
        this.matrix = matrix;
    }

    public DynamicMatrix getMatrix()
    {
        return matrix;
    }

    public void setMatrix(DynamicMatrix matrix)
    {
        this.matrix = matrix;
    }

    /**
     * Analyze cycle dependencies and add parent related cells to the parent dependency collection.
     *
     * @param deep analyze deep.
     * @param visited collection of the analyzing cells which store the deep.
     * @param i cell row index.
     * @param j cell column index.
     * @throws Exception cycle dependencies exception.
     */
    private void analyse(int deep, int visited[][], int i, int j) throws Exception
    {
        //TODO: do smth with the 2d array.
        visited[i][j] = deep;
        Cell cellIJ = matrix.getElement(i, j);
        if(cellIJ.getType().equals(CellType.EXPRESSION))
        {
            for(CellId cellId : cellIJ.getChildrenCellDependencies())
            {

                Pair<Integer, Integer> cellIndexes = CellId.cellIdToIndexes(cellId);
                Cell cell = matrix.getElement(cellId);
                cell.getParentCellDependencies().add(cellIJ.getCellId());
                cell.getParentCellDependencies().addAll(cellIJ.getParentCellDependencies());
                int deepValue = visited[cellIndexes.getFirst()][cellIndexes.getSecond()];
                // If the current dependency's deep is less then the child dependencies deep it means that current cell links to to parent cell in the tree, so make cycle dependencies.
                // The deep 0 means cell is not analyzed yet, so it's a not a cycle dependencies.
                if(deepValue < deep && deepValue != 0)
                {
                    throw new Exception("Detected cycle dependencies");
                }
                else
                {
                    analyse(deep + 1, visited, cellIndexes.getFirst(), cellIndexes.getSecond());
                }
            }
        }
    }

    /**
     * Build parent dependency trees. Parent dependency of the current cell is a cell collection which reference to the current cell.
     */
    public void buildDependencyTrees()
    {
        for(int i = 0; i < matrix.getRowSize(); i++)
        {
            for(int j = 0; j < matrix.getColumnSize(); j++)
            {
                // TODO: Change on List<List>
                int[][] visited = new int[matrix.getRowSize()][matrix.getColumnSize()];
                try
                {
                    analyse(INITIAL_DEEP, visited, i, j);
                }
                catch(Exception e)
                {
                    log.error("Detected cycle dependencies of the cell " + matrix.getElement(i,j).getCellId(), e);
                }
            }
        }
    }

    /**
     * Check the cycle dependencies of the cell.
     * @param i row index.
     * @param j column index.
     * @return cycle dependency cell flag.
     */
    public boolean isCycleDependencies(int i, int j)
    {
        return matrix.getElement(i, j).getParentCellDependencies().contains(matrix.getElement(i, j).getCellId());
    }

    /**
     * Resolve the cycle dependencies problem by setting cycle dependencies cell type.
     */
    public void resolveCycleDependencies()
    {
        for(int i = 0; i < matrix.getRowSize(); i++)
        {
            for(int j = 0; j < matrix.getColumnSize(); j++)
            {
                if(isCycleDependencies(i, j))
                {
                    for(CellId cellId : matrix.getElement(i, j).getParentCellDependencies())
                    {
                        matrix.getElement(cellId).setErrorType(ErrorMessage.CYCLE_DEPENDENCIES);
                    }
                }
            }
        }
    }

     /**
     * Return calculated output cell value.
     * @param i row cell.
     * @param j column cell.
     * @param childDependencyValues map of the children (string cell id - calculated string value) information.
     * @return string value.
     */
    private String calculateCell(int i, int j, Map<String, String> childDependencyValues)
    {
        if(i > matrix.getRowSize() || j > matrix.getColumnSize())
        {
            throw new ArrayIndexOutOfBoundsException("Out of bound exception.");
        }
        Cell currentCell = matrix.getElement(i, j);
        if(currentCell.getType().equals(CellType.EXPRESSION))
        {

            for(CellId childrenCellId : currentCell.getChildrenCellDependencies())
            {
                final Pair<Integer, Integer> childrenCellIndex = CellId.cellIdToIndexes(childrenCellId);
                if(childDependencyValues.get(childrenCellId.toString()) == null)
                {
                    childDependencyValues.put(childrenCellId.toString(), calculateCell(childrenCellIndex.getFirst(), childrenCellIndex.getSecond(), childDependencyValues));
                }
            }
            currentCell.calculateValue(childDependencyValues);
            childDependencyValues.put(currentCell.getCellId().toString(), currentCell.getValue());
            return currentCell.getValue();
        }
        else
        {
            return currentCell.getValue();
        }
    }

    /**
     * Calculate displayed cell values.
     */
    public void calculationTable()
    {
        Map<String, String> childDependencyValues = new HashMap();
        for(int i = 0; i < matrix.getRowSize(); i++)
        {
            for(int j = 0; j < matrix.getColumnSize(); j++)
            {
                Cell cell = matrix.getElement(i, j);
                if(cell.getType().equals(CellType.EXPRESSION))
                {
                    calculateCell(i, j, childDependencyValues);
                }
            }
        }
    }


}
