package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class CommitAst extends Ast{

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("COMMIT");
    }

}
