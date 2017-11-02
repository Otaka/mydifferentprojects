package com.webscrapper.rules;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class UrlRule {

    private final String url;
    private final List<AbstractRule> innerRules = new ArrayList<>();

    public UrlRule(String url, AbstractRule...innerRules) {
        this.url = url;
        for(AbstractRule rule:innerRules){
            this.innerRules.add(rule);
        }
    }

    public String getUrl() {
        return url;
    }

    public List<AbstractRule> getInnerRules() {
        return innerRules;
    }

    public UrlRule addInnerRule(AbstractRule innerRule) {
        innerRules.add(innerRule);
        return this;
    }
}
