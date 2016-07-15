package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class SelectAst extends Ast {

    private boolean distinct = false;
    private FieldListAST fieldList;
    private List<JoinAst> joins = new ArrayList<JoinAst>();
    private FromAst from;
    private WhereAst where;
    private GroupByAst groupBy;
    private OrderByAst orderBy;
    private boolean isSubquery;

    public void setIsSubquery(boolean isSubquery) {
        this.isSubquery = isSubquery;
    }

    public List<JoinAst> getJoins() {
        return joins;
    }

    public void setGroupBy(GroupByAst groupBy) {
        this.groupBy = groupBy;
    }

    public void setOrderBy(OrderByAst orderBy) {
        this.orderBy = orderBy;
    }

    public GroupByAst getGroupBy() {
        return groupBy;
    }

    public OrderByAst getOrderBy() {
        return orderBy;
    }

    public void setWhere(WhereAst where) {
        this.where = where;
    }

    public WhereAst getWhere() {
        return where;
    }

    public void setFrom(FromAst from) {
        this.from = from;
    }

    public FromAst getFrom() {
        return from;
    }

    public void addJoin(JoinAst join) {
        joins.add(join);
    }

    public void setFieldList(FieldListAST fieldList) {
        this.fieldList = fieldList;
    }

    public FieldListAST getFieldList() {
        return fieldList;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        if (isSubquery) {
            context.print("(");
        }
        context.print("SELECT ");
        if (distinct) {
            context.print("DISTINCT ");
        }

        context.print(fieldList)
                .print(" ")
                .print(from);
        if (joins != null && !joins.isEmpty()) {
            context.print(" ").printListOfAsts(joins, " ");
        }

        if (where != null) {
            context.print(" ").print(where);
        }

        if (groupBy != null) {
            context.print(" ").print(groupBy);
        }

        if (orderBy != null) {
            context.print(" ").print(orderBy);
        }

        if (isSubquery) {
            context.print(")");
        }
    }
}
