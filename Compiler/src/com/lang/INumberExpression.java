package com.lang;

/**
 * @author Dmitry
 */
public class INumberExpression extends NumberExpression {

    private final long value;

    public INumberExpression(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
    
    
}
