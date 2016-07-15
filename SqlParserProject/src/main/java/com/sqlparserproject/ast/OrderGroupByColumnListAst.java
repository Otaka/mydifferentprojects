package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class OrderGroupByColumnListAst extends Ast {

    private List<OrderGroupByFieldAst> fields = new ArrayList<OrderGroupByFieldAst>();

    public void add(OrderGroupByFieldAst field) {
        fields.add(field);
    }

    public List<OrderGroupByFieldAst> getFields() {
        return fields;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printListOfAsts(fields, ",");
    }

}
