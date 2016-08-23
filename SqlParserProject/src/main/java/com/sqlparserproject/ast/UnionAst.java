package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class UnionAst extends Ast {

    private SelectAst left;
    private SelectAst right;
    private boolean unionAll = false;

    public UnionAst(SelectAst left) {
        this.left = left;
    }

   

    public void setUnionAll(boolean unionAll) {
        this.unionAll = unionAll;
    }


    public void setRight(SelectAst right) {
        this.right = right;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        left.formatSql(context);
        context.newLine().print("UNION ");
        if (unionAll) {
            context.print("ALL ");
        }
        context.newLine();
        right.formatSql(context);
    }

}
