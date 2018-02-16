package com.expressionlayout.interpreter;

/**
 * @author sad
 */
public class NumberObject {

    private Object value;
    public boolean isDouble = false;
    private String suffix;

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public NumberObject clearSuffix() {
        this.suffix = null;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public NumberObject(long value) {
        this.value = value;
        isDouble = false;
    }

    public NumberObject(double value) {
        this.value = value;
        isDouble = true;
    }

    public NumberObject() {
        value = (Long) 0l;
    }

    public NumberObject makeClone() {
        NumberObject no = new NumberObject();
        no.value = value;
        no.isDouble = isDouble;
        no.suffix=suffix;
        return no;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public long getAsLong() {
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Double) {
            return (long) (double) (Double) value;
        }

        throw new IllegalStateException("Unexpected object value [" + value.getClass() + "] should be Long or Double");
    }

    public double getAsDouble() {
        if (value instanceof Long) {
            return (double) (long) (Long) value;
        }
        if (value instanceof Double) {
            return (Double) value;
        }

        throw new IllegalStateException("Unexpected object value [" + value.getClass() + "] should be Long or Double");
    }

    public NumberObject set(long value) {
        this.value = value;
        isDouble = false;
        return this;
    }

    public NumberObject set(double value) {
        this.value = value;
        isDouble = true;
        return this;
    }

    public void add(NumberObject no) {
        if (isDouble || no.isDouble()) {
            double lhs = getAsDouble();
            double rhs = no.getAsDouble();
            set(lhs + rhs);
        } else {
            long lhs = getAsLong();
            long rhs = no.getAsLong();
            set(lhs + rhs);
        }
    }

    public void minus(NumberObject no) {
        if (isDouble || no.isDouble()) {
            double lhs = getAsDouble();
            double rhs = no.getAsDouble();
            set(lhs - rhs);
        } else {
            long lhs = getAsLong();
            long rhs = no.getAsLong();
            set(lhs - rhs);
        }
    }

    public void mul(NumberObject no) {
        if (isDouble || no.isDouble()) {
            double lhs = getAsDouble();
            double rhs = no.getAsDouble();
            set(lhs * rhs);
        } else {
            long lhs = getAsLong();
            long rhs = no.getAsLong();
            set(lhs * rhs);
        }
    }

    public void div(NumberObject no) {
        if (isDouble || no.isDouble()) {
            double lhs = getAsDouble();
            double rhs = no.getAsDouble();
            set(lhs / rhs);
        } else {
            long lhs = getAsLong();
            long rhs = no.getAsLong();
            set(lhs / rhs);
        }
    }

    @Override
    public String toString() {
        if (isDouble) {
            return Double.toString(getAsDouble());
        } else {
            return Long.toString(getAsLong());
        }
    }

}
