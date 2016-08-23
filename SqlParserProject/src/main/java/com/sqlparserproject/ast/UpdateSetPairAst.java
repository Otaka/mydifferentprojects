package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class UpdateSetPairAst extends Ast {

    private String fieldName;
    private Ast expression;

    public UpdateSetPairAst(String fieldName, Ast expression) {
        this.fieldName = fieldName;
        this.expression = expression;
    }

    public Ast getExpression() {
        return expression;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(fieldName).print("=").print(expression);
    }

}
