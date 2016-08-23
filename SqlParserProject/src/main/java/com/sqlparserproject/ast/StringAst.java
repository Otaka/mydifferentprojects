package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class StringAst extends Ast {

    private String value;

    public StringAst(String value) {
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
        context.print("'").print(value).print("'");
    }

    
}
