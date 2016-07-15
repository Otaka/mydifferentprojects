package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class WhereAst extends Ast{
    private Ast checkExpression;

    public WhereAst(Ast checkExpression) {
        this.checkExpression = checkExpression;
    }

    public Ast getCheckExpression() {
        return checkExpression;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("WHERE ");
        checkExpression.formatSql(context);
    }
    
}
