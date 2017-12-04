package com.sqlprocessor.table;

/**
 * @author sad
 */
public class SqlField {

    private SqlTable table;
    private String field;

    public SqlField(SqlTable table, String field) {
        this.table = table;
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public SqlTable getTable() {
        return table;
    }

    @Override
    public String toString() {
        return table+":"+field;
    }

}
