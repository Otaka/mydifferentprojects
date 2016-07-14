package com.lang;

/**
 * @author Dmitry
 */
public abstract class Expression {
    private boolean primitive;
    
    public boolean isPrimitive() {
        return primitive;
    }

    protected final void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }
    
}
