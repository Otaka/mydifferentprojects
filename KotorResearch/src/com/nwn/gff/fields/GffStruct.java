package nwn.gff.fields;

import nwn.gff.GffLoadContext;
import nwn.gff.GffStructure;

/**
 * @author sad
 */
public class GffStruct extends GffFieldValue {

    private GffStructure value;

    public GffStructure getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int offset) {
        value = loadContext.getStructs()[offset];
    }
}
