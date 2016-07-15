package com.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Dmitry
 */
public class SExpression extends Expression {

    private char bracketSymbol;
    private final List<Expression> expressions = new ArrayList<>();

    public SExpression(Collection expressions) {
        this.expressions.addAll(expressions);
    }

    public SExpression setBracketSymbol(char bracketSymbol) {
        this.bracketSymbol = bracketSymbol;
        return this;
    }

    public char getBracketSymbol() {
        return bracketSymbol;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public Expression getHead() {
        if (expressions.isEmpty()) {
            return null;
        }

        return expressions.get(0);
    }
}
