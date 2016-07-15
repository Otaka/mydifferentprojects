package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class NullAst extends Ast {

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("NULL");
    }

}
