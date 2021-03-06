package com.nwn.data.gff;

import com.nwn.data.gff.fields.GffField;

/**
 * @author sad
 */
public class GffStructure {
    private int type;
    private GffField[] fields;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFields(GffField[] fields) {
        this.fields = fields;
    }

    public GffField[] getFields() {
        return fields;
    }

}
