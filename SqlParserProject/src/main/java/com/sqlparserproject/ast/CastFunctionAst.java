package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class CastFunctionAst extends Ast{
    private Ast checkExpression;
    private SqlType type;

    public CastFunctionAst(Ast checkExpression, SqlType type) {
        this.checkExpression = checkExpression;
        this.type = type;
    }

    public Ast getCheckExpression() {
        return checkExpression;
    }

    public SqlType getType() {
        return type;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("CAST(").print(checkExpression).print(" AS ").print(type.toString()).print(")");
    }
    
}
