package com.simplecas4j.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry
 */
public class Rule {

    private RuleType type;
    private List<Rule> children;
    private String value;
    private List<String> attributes = new ArrayList<>();
    private String description;
    private String label;
    /**
    Same_as_label it is mechanism that allows you to implement following rules: a/a =1 or (a+b)+(a+b)=2(a+b); It allows to search identical expressions
     */
    private String sameAsLabel;

    public String getDescription() {
        return description;
    }

    public Rule setDescription(String description) {
        this.description = description;
        return this;
    }

    public Rule setSameAsLabel(String sameAsLabel) {
        this.sameAsLabel = sameAsLabel;
        return this;
    }

    public String getSameAsLabel() {
        return sameAsLabel;
    }

    public Rule(RuleType ruletype) {
        this.type = ruletype;
    }

    public Rule(RuleType ruleType, List<Rule> children) {
        this.type = ruleType;
        this.children = children;
    }

    public Rule() {
    }

    public Rule setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Rule addAttributes(String... attributes) {
        Collections.addAll(this.attributes, attributes);
        return this;
    }

    public Rule setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Rule setRuleType(RuleType type) {
        this.type = type;
        return this;
    }

    public RuleType getType() {
        return type;
    }

    public Rule setChildren(List<Rule> children) {
        this.children = children;
        return this;
    }

    public List<Rule> getChildren() {
        return children;
    }

    public Rule children(Rule... rules) {
        children = new ArrayList();
        Collections.addAll(children, rules);
        return this;
    }

    public Rule addChild(Rule rule) {
        if (children == null) {
            children = new ArrayList();
        }

        children.add(rule);
        return this;
    }

    @Override
    public String toString() {
        return getType() + "(" + getValue() + ")";
    }

}
