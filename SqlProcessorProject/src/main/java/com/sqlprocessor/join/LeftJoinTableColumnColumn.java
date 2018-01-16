package com.sqlprocessor.join;

import com.sqlprocessor.table.SqlTable;
import net.sf.jsqlparser.expression.Expression;

/**
 * @author sad
 */
public class LeftJoinTableColumnColumn {

    private SqlTable table;
    private String keyField;
    private SqlTable dependentTable;
    private String dependentField;

    public LeftJoinTableColumnColumn(SqlTable table,String keyField, SqlTable dependentTable, String dependentField) {
        this.table = table;
        this.keyField = keyField;
        this.dependentTable = dependentTable;
        this.dependentField = dependentField;
    }

    public String getDependentField() {
        return dependentField;
    }

    public SqlTable getDependentTable() {
        return dependentTable;
    }

    public String getKeyField() {
        return keyField;
    }

    public SqlTable getTable() {
        return table;
    }

    @Override
    public String toString() {
        return table+":"+keyField+"  "+dependentTable+":"+dependentField;
    }
    
}
