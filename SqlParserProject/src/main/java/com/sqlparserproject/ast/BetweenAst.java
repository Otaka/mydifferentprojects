package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class BetweenAst extends Ast {

    private Ast value;
    private NumberAst leftBoundary;
    private NumberAst rightBoundary;

    public BetweenAst(Ast value, NumberAst leftBoundary, NumberAst rightBoundary) {
        this.value = value;
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
    }

    public NumberAst getLeftBoundary() {
        return leftBoundary;
    }

    public NumberAst getRightBoundary() {
        return rightBoundary;
    }

    public Ast getValue() {
        return value;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(value)
                .print(" BETWEEN ")
                .print(leftBoundary)
                .print(" AND ")
                .print(rightBoundary);
    }

}
