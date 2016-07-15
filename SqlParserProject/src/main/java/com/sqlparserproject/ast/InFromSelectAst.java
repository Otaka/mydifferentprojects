package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class InFromSelectAst extends Ast {

    private SimpleFieldAst field;
    private SelectAst select;
    private boolean not;

    public InFromSelectAst(SimpleFieldAst field, SelectAst select) {
        this.field = field;
        this.select = select;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SimpleFieldAst getField() {
        return field;
    }

    public SelectAst getSelect() {
        return select;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(field);
        if(not){
            context.print(" NOT");
        }
        context.print(" IN ")
                .print(select);
    }

}
