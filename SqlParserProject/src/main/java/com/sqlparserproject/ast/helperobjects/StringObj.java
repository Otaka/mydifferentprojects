package com.sqlparserproject.ast.helperobjects;

/**
 * @author sad
 */
public class StringObj {

    private String val;
    private String label;

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
        return label+":"+val;
    }

}
