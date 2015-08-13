package com.nwn.gff.fields;

import java.io.IOException;
import com.nwn.BaseReader;
import com.nwn.NwnByteArrayInputStream;
import com.nwn.gff.GffLoadContext;

/**
 * @author sad
 */
public class GffExoLocString extends GffFieldValue {

    private GffExoLocSubString[] value;
    private int stringRef;

    public GffExoLocSubString[] getValue() {
        return value;
    }

    @Override
    public void load(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        NwnByteArrayInputStream rawStream = loadContext.getRawData();
        rawStream.setPosition(dataOrOffset);
        int totalSize = BaseReader.readInt(rawStream);
        stringRef = BaseReader.readInt(rawStream);
        int subStringsCount = BaseReader.readInt(rawStream);
        value = new GffExoLocSubString[subStringsCount];
        for (int i = 0; i < subStringsCount; i++) {
            GffExoLocSubString subString = readExoString(rawStream);
            value[i] = subString;
        }
    }

    private GffExoLocSubString readExoString(NwnByteArrayInputStream stream) throws IOException {
        int stringId = BaseReader.readInt(stream);
        int size = BaseReader.readInt(stream);
        String stringValue = BaseReader.readString(stream, size);
        GffExoLocSubString subString = new GffExoLocSubString(stringId, stringValue);
        return subString;
    }

    public static class GffExoLocSubString {

        private final int stringId;
        private final String value;

        public GffExoLocSubString(int stringId, String value) {
            this.stringId = stringId;
            this.value = value;
        }

        public int getStringId() {
            return stringId;
        }

        public String getValue() {
            return value;
        }

    }
}
