package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class OverPartitionByAst extends Ast {

    private List<String> fields = new ArrayList<String>();
    private OrderByAst orderBy;

    public void setOrderBy(OrderByAst orderBy) {
        this.orderBy = orderBy;
    }

    public OrderByAst getOrderBy() {
        return orderBy;
    }

    public void add(String field) {
        fields.add(field);
    }

    public List<String> getFields() {
        return fields;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("PARTITION BY ").printList(fields, ",");
        if(orderBy!=null){
            context.print(" ").print(orderBy);
        }
    }

}
