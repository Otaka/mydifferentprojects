package nwn.gff.fields;

import java.io.IOException;
import nwn.BaseReader;
import nwn.gff.GffLoadContext;

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
