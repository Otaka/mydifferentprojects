package com.simplecas4j.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry
 */
public class Rule {

    private String name;
    private List<Rule> children;
    private String value;
    private List<String> attributes = new ArrayList<>();
    private String label;

    public Rule(String name) {
        this.name = name;
    }

    public Rule(String name, List<Rule> children) {
        this.name = name;
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

    public Rule setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
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
}
