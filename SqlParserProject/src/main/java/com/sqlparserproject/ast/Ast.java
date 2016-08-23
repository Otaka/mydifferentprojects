package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public abstract class Ast {
    public abstract void formatSql(SqlFormatterContext context);
}
