package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class NumberAst extends Ast{
    private String value;

    public NumberAst(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(value);
    }
    
    
}
