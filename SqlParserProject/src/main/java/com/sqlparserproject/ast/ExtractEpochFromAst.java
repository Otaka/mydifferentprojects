package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class ExtractEpochFromAst extends AstWithInternalAst {

    public ExtractEpochFromAst(Ast internal) {
        super(internal);
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("EXTRACT (EPOCH FROM ").print(getInternal()).print(")");
    }
}