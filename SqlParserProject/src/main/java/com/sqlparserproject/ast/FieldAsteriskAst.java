package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class FieldAsteriskAst extends FieldAst{

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("*");
    }

}
