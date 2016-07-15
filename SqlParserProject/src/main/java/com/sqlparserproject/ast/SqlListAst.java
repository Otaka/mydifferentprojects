package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class SqlListAst extends Ast {

    private List<Ast> sqls = new ArrayList<>();

    public void add(Ast ast) {
        sqls.add(ast);
    }

    public List<Ast> getSqls() {
        return sqls;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printListOfAsts(sqls, ";\n\n");
        context.print(";");
    }
}
