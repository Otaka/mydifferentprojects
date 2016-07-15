package com.nwn.data.gff.fields;

import java.io.IOException;
import com.nwn.data.BaseReader;
import com.nwn.data.gff.GffLoadContext;

/**
 * @author sad
 */
public class GffDWord64 extends GffFieldValue {

    private long value;

    public long getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        loadContext.getRawData().setPosition(dataOrOffset);
        value = BaseReader.readLong(loadContext.getRawData());
    }
}
