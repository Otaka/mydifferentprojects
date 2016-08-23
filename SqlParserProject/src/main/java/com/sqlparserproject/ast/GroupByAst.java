package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class GroupByAst extends Ast {

    private OrderGroupByColumnListAst fields;

    public void setFields(OrderGroupByColumnListAst fields) {
        this.fields = fields;
    }

    public OrderGroupByColumnListAst getFields() {
        return fields;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("GROUP BY ").print(fields);
    }
}
