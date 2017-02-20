package com.simplepl.grammar.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharMatcher;

public class JavaUnicodeMatcherStartString extends CharMatcher {

    public JavaUnicodeMatcherStartString() {
        super('.');
    }

    @Override
    public boolean match(MatcherContext context) {
        if (!Character.isJavaIdentifierStart(context.getCurrentChar())) {
            return false;
        }
        context.advanceIndex(1);
        context.createNode();
        return true;
    }
}
