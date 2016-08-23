package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class CaseAst extends Ast {

    private List<CaseRuleAst> cases = new ArrayList<CaseRuleAst>();
    private Ast elseRule;

    public void addCaseRule(CaseRuleAst rule) {
        cases.add(rule);
    }

    public void setElseRule(Ast elseRule) {
        this.elseRule = elseRule;
    }

    public List<CaseRuleAst> getCases() {
        return cases;
    }

    public Ast getElseRule() {
        return elseRule;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("CASE ").printListOfAsts(cases, " ");
        if(elseRule!=null){
            context.print(" ELSE ").print(elseRule);
        }

        context.print(" END");
    }

}
