package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class DropTableAST extends Ast {

    private String tableName;
    private boolean ifExists = false;
    private boolean cascade = false;

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public boolean isCascade() {
        return cascade;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("DROP TABLE ");
        if (ifExists) {
            context.print("IF EXISTS ");
        }
        context.print(tableName);
        if (cascade) {
            context.print(" CASCADE");
        }
    }

}
