package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class SimpleFieldAst extends Ast {

    private String table;
    private String name;

    public SimpleFieldAst(String table, String fieldName) {
        this.table = table;
        this.name = fieldName;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    @Override
    public String toString() {
        if (table != null) {
            return table + "." + name;
        }

        return name;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(toString());
    }
}
