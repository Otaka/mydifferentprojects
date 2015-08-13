package com.nwn.gff.fields;

import com.nwn.BaseReader;
import com.nwn.gff.GffLoadContext;
import java.io.IOException;

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
