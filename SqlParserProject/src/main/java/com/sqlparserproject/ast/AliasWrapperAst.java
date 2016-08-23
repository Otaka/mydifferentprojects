package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class AliasWrapperAst extends FieldAst {

    private Ast internal;
    private String alias;

    private boolean isTable;

    public AliasWrapperAst(Ast internal, String alias, boolean isTable) {
        this.internal = internal;
        this.alias = alias;
        this.isTable = isTable;
    }

    public String getAlias() {
        return alias;
    }

    public Ast getInternal() {
        return internal;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(internal);
        if (SqlFormatterContext.stringNotEmpty(alias)) {
            if (isTable) {
                context.print(" ").print(alias);
            } else {
                context.print(" as ").print(alias);
            }
        }
    }

}
