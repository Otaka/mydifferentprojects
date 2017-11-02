package com.nwn.data.gff.fields;

import java.io.IOException;
import com.nwn.data.gff.GffLoadContext;

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
