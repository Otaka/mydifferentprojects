package com.nwn.data.gff.fields;

import com.nwn.data.gff.GffLoadContext;
import com.nwn.data.gff.GffStructure;

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
