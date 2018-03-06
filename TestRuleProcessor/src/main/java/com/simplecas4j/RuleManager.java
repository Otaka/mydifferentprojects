package com.simplecas4j;

import com.simplecas4j.rule.Rule;
import com.simplecas4j.rule.RuleReplacement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class RuleManager {

    private final List<RuleAndReplacementPair> rulePairs = new ArrayList<>();

    public RuleManager() {
        initRules();
    }

    public void initRules() {
        addRule(operator("+", number("0").setLabel("zeroLiteral"), anyExpression()), replacement(removeLabeledGroup("zeroLiteral")));
        addRule(operator("*", number("0"), anyExpression().setLabel("expression")), replacement(removeLabeledGroup("expression")));
        addRule(operator("*", number("1").setLabel("literal"), anyExpression().setLabel("expression")), replacement(removeLabeledGroup("literal")));
        addRule(orderedOperator("*", variable().setLabel("var"), number().setLabel("literal")), replacement(swap("literal", "var")));
        addRule(orderedOperator("+", number().setLabel("literal"), variable().setLabel("var")), replacement(swap("var", "literal")));
    }

    public RuleReplacement removeLabeledGroup(String label) {
        return (ast) -> {
            System.out.println("Remove label [" + label + "]");
        };
    }

    public RuleReplacement swap(String label1, String label2) {
        return (ast) -> {
            System.out.println("Swap [" + label1 + "] with [" + label2 + "]");
        };
    }

    public RuleReplacement[] replacement(RuleReplacement... replacementRules) {
        return replacementRules;
    }

    public Rule number(String value) {
        return new Rule().setValue(value).setName(EquationExecutor.NUMBER).addAttributes("literal", "number");
    }

    public Rule number() {
        return new Rule().setValue(EquationExecutor.ANY).setName(EquationExecutor.NUMBER).addAttributes("literal", "number");
    }

    public Rule anyExpression() {
        return new Rule().setName(EquationExecutor.ANY);
    }

    public Rule noMore() {
        return new Rule().setName(EquationExecutor.NO_MORE);
    }

    public Rule variable() {
        return new Rule().setName(EquationExecutor.VAR);
    }

    public Rule operator(String operator, Rule... children) {
        return new Rule().setValue(operator).setName(EquationExecutor.OPERATOR).children(children);
    }

    public Rule orderedOperator(String operator, Rule... children) {
        return new Rule().setValue(operator).setName(EquationExecutor.ORDERED_OPERATOR).children(children);
    }

    public void addRule(Rule rule, RuleReplacement[] ruleReplacement) {
        RuleAndReplacementPair rrp = new RuleAndReplacementPair();
        rrp.rule = rule;
        rrp.ruleReplacement = ruleReplacement;
        rulePairs.add(rrp);
    }

    private static class RuleAndReplacementPair {

        private Rule rule;
        private RuleReplacement[] ruleReplacement;
    }
}
