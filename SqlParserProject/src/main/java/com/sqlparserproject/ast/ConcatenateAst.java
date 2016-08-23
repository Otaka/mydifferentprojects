package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class ConcatenateAst extends Ast {

    private Ast left;
    private Ast right;

    public ConcatenateAst(Ast left, Ast right) {
        this.left = left;
        this.right = right;
    }

    public Ast getLeft() {
        return left;
    }

    public Ast getRight() {
        return right;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        left.formatSql(context);
        context.print("||");
        right.formatSql(context);
    }

}
