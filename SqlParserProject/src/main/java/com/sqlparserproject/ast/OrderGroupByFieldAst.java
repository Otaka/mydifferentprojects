package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class OrderGroupByFieldAst extends Ast{

    private Ast field;
    private String order;

    public void setField(Ast field) {
        this.field = field;
    }

    public Ast getField() {
        return field;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(field);
        if(SqlFormatterContext.stringNotEmpty(order)){
            context.print(" ").print(order);
        }
    }

}
