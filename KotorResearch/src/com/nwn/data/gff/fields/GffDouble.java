package com.nwn.data.gff.fields;

import java.io.IOException;
import com.nwn.data.BaseReader;
import com.nwn.data.gff.GffLoadContext;

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
