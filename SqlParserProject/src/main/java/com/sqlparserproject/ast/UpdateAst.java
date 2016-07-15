package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class UpdateAst extends Ast {

    private String tableName;
    private UpdateListOfFieldAst listOfFields;
    private FromAst from;
    private WhereAst whereAst;

    public void setFrom(FromAst from) {
        this.from = from;
    }

    public void setWhereAst(WhereAst whereAst) {
        this.whereAst = whereAst;
    }

    public FromAst getFrom() {
        return from;
    }

    public WhereAst getWhereAst() {
        return whereAst;
    }

    public void setListOfFields(UpdateListOfFieldAst listOfFields) {
        this.listOfFields = listOfFields;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UpdateListOfFieldAst getListOfFields() {
        return listOfFields;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("UPDATE ")
                .print(tableName)
                .print(" SET ")
                .print(listOfFields);
        if (from != null) {
            context.print(" ").print(from);
        }

        if (whereAst != null) {
            context.print(" ").print(whereAst);
        }
    }
}
