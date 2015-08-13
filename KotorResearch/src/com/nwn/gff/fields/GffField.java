package com.nwn.gff.fields;

/**
 * @author sad
 */
public class GffField {
    private GffFieldValue value;
    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(GffFieldValue value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public GffFieldValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getLabel() + ":" + (value == null ? "null" : value.toString());
    }

}
