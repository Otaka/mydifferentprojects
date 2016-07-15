package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class InsertAst extends Ast {

    private String tableName;
    private InsertColumnListAst columnList;
    private SelectAst asSelect;

    public void setAsSelect(SelectAst asSelect) {
        this.asSelect = asSelect;
    }

    public SelectAst getAsSelect() {
        return asSelect;
    }

    public void setColumnList(InsertColumnListAst columnList) {
        this.columnList = columnList;
    }

    public InsertColumnListAst getColumnList() {
        return columnList;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("INSERT INTO ").print(tableName);
        if (columnList != null) {
            context.print("(").print(columnList).print(")");
        }

        if (asSelect != null) {
            context.print(" ").print(asSelect);
        } else {
            throw new RuntimeException("insert currently supports only such pattern 'insert into table(field...) select * from...'");
        }
    }
}
