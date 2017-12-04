package com.nwn.data.gff.fields;

import java.io.IOException;
import com.nwn.data.BaseReader;
import com.nwn.data.gff.GffLoadContext;

/**
 * @author sad
 */
public class GffVoid extends GffFieldValue {

    private byte[] value;

    public byte[] getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        loadContext.getRawData().setPosition(dataOrOffset);
        int size = BaseReader.readInt(loadContext.getRawData());
        value = new byte[size];
        loadContext.getRawData().read(value);
    }
}
