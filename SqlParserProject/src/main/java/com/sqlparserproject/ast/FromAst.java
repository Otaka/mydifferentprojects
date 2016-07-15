package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FromAst extends Ast {

    private List<Ast> rules = new ArrayList<>();

    public void addRule(Ast argument) {
        rules.add(argument);
    }

    public List<Ast> getRules() {
        return rules;
    }
    
    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("FROM ").printListOfAsts(rules,",");
    }

}
