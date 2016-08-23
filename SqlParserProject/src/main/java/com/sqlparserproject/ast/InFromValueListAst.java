package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * someting like field in (1,2,3,4,5) or field in ('34','24','14') or f() in (1,2,3,4)
 */
public class InFromValueListAst extends Ast {

    private Ast field;
    private InValueListAst valueList;
    private boolean not = false;

    public InFromValueListAst(Ast field, InValueListAst valueList) {
        this.field = field;
        this.valueList = valueList;
    }

    public boolean isNot() {
        return not;
    }

    
    public void setNot(boolean not) {
        this.not = not;
    }

    public Ast getField() {
        return field;
    }

    public InValueListAst getValueList() {
        return valueList;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(field);
        if(not){
            context.print(" NOT");
        }
        context.print(" IN (")
                .print(valueList)
                .print(")");
    }

}
