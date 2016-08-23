package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class CaseRuleAst extends Ast {

    private Ast conditionRule;
    private Ast bodyRule;

    public CaseRuleAst(Ast conditionRule, Ast bodyRule) {
        this.conditionRule = conditionRule;
        this.bodyRule = bodyRule;
    }

    public Ast getBodyRule() {
        return bodyRule;
    }

    public Ast getConditionRule() {
        return conditionRule;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("WHEN ")
                .print(conditionRule)
                .print(" THEN ")
                .print(bodyRule);
    }
}
