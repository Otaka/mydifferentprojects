package com.nwn.data.gff.fields;

import java.io.IOException;
import com.nwn.data.BaseReader;
import com.nwn.data.gff.GffLoadContext;

/**
 * @author sad
 */
public class GffResRef extends GffFieldValue {

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        loadContext.getRawData().setPosition(dataOrOffset);
        int size = loadContext.getRawData().read() & 0xFF;
        value = BaseReader.readString(loadContext.getRawData(), size);
    }
}
