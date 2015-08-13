package com.nwn.gff.fields;

import com.nwn.BaseReader;
import com.nwn.gff.GffLoadContext;
import java.io.IOException;

/**
 * @author sad
 */
public class GffDouble extends GffFieldValue {

    private double value;

    public double getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        loadContext.getRawData().setPosition(dataOrOffset);
        value = Double.longBitsToDouble(BaseReader.readLong(loadContext.getRawData()));
    }
}
