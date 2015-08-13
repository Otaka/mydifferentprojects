package com.nwn.gff.fields;

import com.nwn.gff.GffLoadContext;
import java.io.IOException;

/**
 * @author sad
 */
public class GffFloat extends GffFieldValue {

    private float value;

    public float getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        value = Float.intBitsToFloat(dataOrOffset);//TODO: test
    }
}
