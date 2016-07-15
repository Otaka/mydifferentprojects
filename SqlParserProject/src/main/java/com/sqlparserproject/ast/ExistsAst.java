package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class ExistsAst extends AstWithInternalAst {

    public ExistsAst(Ast internal) {
        super(internal);
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("EXISTS")
                .print(getInternal());
    }

}
