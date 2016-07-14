package com.lang;

/**
 * @author Dmitry
 */
public class StringExpression extends Expression {

    private final String value;

    public StringExpression(String value) {
        this.value = value;
        setPrimitive(true);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    
}
