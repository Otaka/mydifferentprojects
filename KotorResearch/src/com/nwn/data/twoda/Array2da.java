package com.nwn.data.twoda;

import java.util.List;

/**
 * @author sad
 */
public class Array2da {
    private List<String> columnNames;
    private List<String[]> rowData;

    public Array2da() {
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String[]> getRowData() {
        return rowData;
    }

    public void setRowData(List<String[]> rowData) {
        this.rowData = rowData;
    }

}
