package com.simple.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 22.02.16.
 */
public class DynamicMatrix {
    private List<List<Cell>> cells;
    private int rowSize;
    private int columnSize;

    public DynamicMatrix(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;

        cells = new ArrayList();
        for (int i = 0; i<rowSize; i++)
        {
            cells.add(new ArrayList<Cell>());
            for (int j = 0; j<columnSize; j++)
            {
                cells.get(i).add(new Cell(i, j));
            }
        }
    }

    /**
     * Get dynamic matrix.
     * @return dynamic matrix.
     */
    public List<List<Cell>> getCells() {
        return cells;
    }

    public int getRowSize() {
        return rowSize;
    }

    public void setRowSize(int rowSize) {
        this.rowSize = rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    /**
     * Get cell of the matrix.
     * @param i row index.
     * @param j column index.
     * @return element of the matrix.
     */
    public Cell getElement (final int i, final int j){
        return cells.get(i).get(j);
    }

    /**
     *  Get cell of the matrix.
     * @param cellId cell id.
     * @returnlement of the matrix.
     */
    public Cell getElement (CellId cellId) {
        Pair<Integer, Integer> cellIdToIndex = CellId.cellIdToIndex(cellId);
        return cells.get(cellIdToIndex.getFirst()).get(cellIdToIndex.getSecond());
    }

    public String printTableIndex() {
        StringBuilder tableIndexBuilder = new StringBuilder("");
        for (int i = 0; i<getRowSize(); i++)
        {
            for (int j = 0; j<getColumnSize(); j++)
            {
                tableIndexBuilder.append(getElement(i, j).getCellId());
                if (j != getColumnSize()-1) {
                    tableIndexBuilder.append("\t");
                }
            }
            if (i != getRowSize()-1) {
                tableIndexBuilder.append("\n");
            }
        }
        return tableIndexBuilder.toString();
    }

    public String printTable() {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i<getRowSize(); i++)
        {
            for (int j = 0; j<getColumnSize(); j++)
            {
                builder.append(getElement(i, j).getValue());
                builder.append("\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String printStringValueTable() {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i<getRowSize(); i++)
        {
            for (int j = 0; j<getColumnSize(); j++)
            {
                builder.append(getElement(i, j).getStringValue());
                if (j != getColumnSize()-1) {
                    builder.append("\t");
                }
            }
            if (i != getRowSize()-1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public void printTableType() {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i<getRowSize(); i++)
        {
            for (int j = 0; j<getColumnSize(); j++)
            {
                builder.append(getElement(i, j).getType());
                builder.append("\t");
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    public void showChildrenCellDependancies() {
        System.out.println("Show children cell dependencies");
        for (int i = 0; i<getRowSize(); i++) {
            for (int j = 0; j < getColumnSize(); j++) {
                getElement(i, j).showChildrenDependencies();
            }
        }
    }

    public void showParentCellDependancies() {
        System.out.println("Show parent cell dependencies");
        for (int i = 0; i<getRowSize(); i++) {
            for (int j = 0; j < getColumnSize(); j++) {
                getElement(i, j).showParentDependencies();
            }
        }
    }
}
