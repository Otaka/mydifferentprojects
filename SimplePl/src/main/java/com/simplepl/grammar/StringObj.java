package com.simplepl.grammar;

/**
 * @author sad
 */
public class StringObj {

    private final String val;
    private final String label;

    public StringObj(String val, String label) {
        this.val = val;
        this.label = label;
    }

    public String getVal() {
        return val;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + ":" + val;
    }

}
