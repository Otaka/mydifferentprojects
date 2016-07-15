package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class TableNameAst extends Ast {

    private String tableName;

    public TableNameAst(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(tableName);

    }

    @Override
    public String toString() {
        return tableName;
    }

}
