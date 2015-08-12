package nwn.gff.fields;

import java.io.IOException;
import nwn.gff.GffLoadContext;
import nwn.gff.GffStructure;

/**
 * @author sad
 */
public class GffList extends GffFieldValue {

    private GffStructure[] structs;

    public GffStructure[] getValue() {
        return structs;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        int[] indicies = loadContext.getListIndicies();
        int offset=dataOrOffset/4;
        int count=indicies[offset];
        structs = new GffStructure[count];
        for (int i = 0; i < count; i++) {
            structs[i] = loadContext.getStructs()[indicies[offset+i+1]];
        }
    }
}
