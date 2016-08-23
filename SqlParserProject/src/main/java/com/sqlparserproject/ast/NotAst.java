package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class NotAst extends AstWithInternalAst {

    public NotAst(Ast internal) {
        super(internal);
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("NOT ")
                .print(getInternal());
    }

}
