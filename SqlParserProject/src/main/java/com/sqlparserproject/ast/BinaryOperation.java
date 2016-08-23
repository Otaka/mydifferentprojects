package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class BinaryOperation extends Ast{
    private String operation;
    private Ast left;
    private Ast right;

    public BinaryOperation(String operation, Ast left, Ast right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    public Ast getLeft() {
        return left;
    }

    public String getOperation() {
        return operation;
    }

    public Ast getRight() {
        return right;
    }

    @Override
    public String toString() {
        return operation;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        left.formatSql(context);
        context.print(" ").print(operation).print(" ");
        right.formatSql(context);
    }
    
}
