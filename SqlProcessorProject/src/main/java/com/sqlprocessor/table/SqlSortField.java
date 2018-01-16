package com.sqlprocessor.table;

/**
 * @author sad
 */
public class SqlSortField {

    private SqlField sqlField;
    private SortOrder sortOrder;

    public SqlSortField(SqlField sqlField, SortOrder sortOrder) {
        this.sqlField = sqlField;
        this.sortOrder = sortOrder;
    }

    public SqlSortField(SqlField sqlField) {
        this.sqlField = sqlField;
        sortOrder = SortOrder.ASC;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public SqlField getSqlField() {
        return sqlField;
    }

    public enum SortOrder {
        ASC, DESC
    }

    @Override
    public String toString() {
        return sqlField+" "+sortOrder;
    }
    
}
