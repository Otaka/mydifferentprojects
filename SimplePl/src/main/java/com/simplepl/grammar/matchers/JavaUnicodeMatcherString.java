package com.simplepl.grammar.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CharMatcher;

public class JavaUnicodeMatcherString extends CharMatcher {
    public JavaUnicodeMatcherString() {
        super('.');
    }

    @Override
    public boolean match(MatcherContext context) {
        if (!Character.isJavaIdentifierPart(context.getCurrentChar())) return false;
        context.advanceIndex(1);
        context.createNode();
        return true;
    }
}
