package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class BooleanAst extends Ast{
    private boolean value;

    public BooleanAst(String value) {
        this.value=Boolean.parseBoolean(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(value?"TRUE":"FALSE");
    }
    
}
