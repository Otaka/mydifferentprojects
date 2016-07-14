package com.lang;

/**
 * @author Dmitry
 */
public class FNumberExpression extends NumberExpression {

    private final double value;

    public FNumberExpression(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

}
