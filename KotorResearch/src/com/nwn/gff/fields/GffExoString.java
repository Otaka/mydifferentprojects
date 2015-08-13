package com.nwn.gff.fields;

import java.io.IOException;
import com.nwn.BaseReader;
import com.nwn.gff.GffLoadContext;
import com.nwn.gff.fields.GffFieldValue;

/**
 * @author sad
 */
public class GffExoString extends GffFieldValue {

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        loadContext.getRawData().setPosition(dataOrOffset);
        int size = BaseReader.readInt(loadContext.getRawData());
        value = BaseReader.readString(loadContext.getRawData(), size);
    }
}
