package com.lang.expressions;

/**
 *
 * @author Dmitry
 */
public class TokenExpression extends Expression {

    private final String name;

    public TokenExpression(String name) {
        this.name = name;
        setPrimitive(true);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
