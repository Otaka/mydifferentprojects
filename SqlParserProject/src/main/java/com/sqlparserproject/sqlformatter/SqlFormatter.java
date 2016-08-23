package com.sqlparserproject.sqlformatter;

import com.sqlparserproject.ast.Ast;

/**
 * @author sad
 */
public class SqlFormatter {
    public String formatSql(Ast ast){
        SqlFormatterContext context=new SqlFormatterContext();
        ast.formatSql(context);
        return context.getResult();
    }
}
