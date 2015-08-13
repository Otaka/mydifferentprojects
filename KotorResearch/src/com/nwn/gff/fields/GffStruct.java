package com.nwn.gff.fields;

import com.nwn.gff.GffLoadContext;
import com.nwn.gff.GffStructure;

/**
 * @author sad
 */
public class GffStruct extends GffFieldValue {

    private GffStructure value;

    public GffStructure getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int offset) {
        value = loadContext.getStructs()[offset];
    }
}
