package nwn.gff.fields;

import java.io.IOException;
import nwn.BaseReader;
import nwn.gff.GffLoadContext;

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
        int size=BaseReader.readInt(loadContext.getRawData());
        value=BaseReader.readString(loadContext.getRawData(), size);
    }
}
