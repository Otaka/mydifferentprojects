package com.nwn.data.gff.fields;

import com.nwn.data.gff.GffLoadContext;

/**
 * @author sad
 */
public class GffDWord extends GffFieldValue {

    private int value;

    public int getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) {
        value = dataOrOffset;
    }
}
