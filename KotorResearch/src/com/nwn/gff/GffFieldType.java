package com.nwn.gff;

import com.nwn.gff.fields.GffExoString;
import java.io.IOException;
import java.util.HashMap;
import com.nwn.gff.fields.*;

/**
 * @author sad
 */
public enum GffFieldType {

    BYTE(0), CHAR(1), WORD(2), SHORT(3), DWORD(4), INT(5), DWORD64(6), INT64(7), FLOAT(8), DOUBLE(9), CEXOSTRING(10), RESREF(11), CEXOLOCSTRING(12), VOID(13), STRUCT(14), LIST(15);

    private final int type;

    private GffFieldType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private static final HashMap<Integer, GffFieldType> typeToEnumMap = new HashMap<Integer, GffFieldType>();

    static {
        for (GffFieldType ft : values()) {
            typeToEnumMap.put(ft.getType(), ft);
        }
    }

    public static GffFieldType getByType(int type) {
        return typeToEnumMap.get(type);
    }

    public static GffFieldValue createGffFieldValue(GffFieldType type) {
        switch (type) {
            case BYTE:
                return new GffByte();
            case CHAR:
                return new GffChar();
            case WORD:
                return new GffWord();
            case SHORT:
                return new GffShort();
            case DWORD:
                return new GffDWord();
            case INT:
                return new GffInt();
            case DWORD64:
                return new GffDWord64();
            case INT64:
                return new GffInt64();
            case FLOAT:
                return new GffFloat();
            case DOUBLE:
                return new GffDouble();
            case CEXOSTRING:
                return new GffExoString();
            case RESREF:
                return new GffResRef();
            case CEXOLOCSTRING:
                return new GffExoLocString();
            case VOID:
                return new GffVoid();
            case STRUCT:
                return new GffStruct();
            case LIST:
                return new GffList();
            default:
                throw new RuntimeException("Unrecognized GffFieldType " + type);
        }
    }

    public GffFieldValue loadGffFieldValue(GffLoadContext loadContext, int dataOrOffset) throws IOException {
        GffFieldValue value = createGffFieldValue(this);
        value.load(loadContext, dataOrOffset);
        return value;
    }

}
