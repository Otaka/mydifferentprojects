package com.simplecas4j.rule;

import com.simplecas4j.rule.Rule;
import com.simplecas4j.rule.RuleReplacement;

/**
 * @author sad
 */
public class RuleAndReplacementPair {

    private Rule rule;
    private RuleReplacement[] ruleReplacement;

    public RuleAndReplacementPair(Rule rule, RuleReplacement[] ruleReplacement) {
        this.rule = rule;
        this.ruleReplacement = ruleReplacement;
    }

    public RuleReplacement[] getRuleReplacement() {
        return ruleReplacement;
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return rule.toString();
    }


}
