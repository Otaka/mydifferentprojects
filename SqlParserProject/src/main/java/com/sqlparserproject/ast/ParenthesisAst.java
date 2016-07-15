package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class ParenthesisAst extends Ast{
    private Ast internal;

    public ParenthesisAst(Ast internal) {
        this.internal = internal;
    }

    public Ast getInternal() {
        return internal;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("(");
        internal.formatSql(context);
        context.print(")");
    }
    
}
