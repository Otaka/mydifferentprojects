package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class TypeConversionAst extends Ast {

    private Ast internal;
    private SqlType sqlType;

    public TypeConversionAst(Ast internal, SqlType sqlType) {
        this.internal = internal;
        this.sqlType = sqlType;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(internal).print("::").print(sqlType.toString());
    }

}
