package com.nwn.key;

import static com.nwn.key.ContentType.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry
 */
public enum ResourceType {

    BMP(1, BINARY),
    TGA(3, BINARY),
    WAV(4, BINARY),
    PLT(6, BINARY),
    INI(7, TEXT_INI),
    TXT(10, TEXT),
    MDL(2002, MDL_MODEL),
    NSS(2009, TEXT),
    NCS(2010, BINARY),
    ARE(2012, GFF),
    SET(2013, TEXT_INI),
    IFO(2014, GFF),
    BIC(2015, GFF),
    WOK(2016, MDL_MODEL),
    _2DA(2017, TEXT),
    TXI(2022, TEXT),
    GIT(2023, GFF),
    UTI(2025, GFF),
    UTC(2027, GFF),
    DLG(2029, GFF),
    ITP(2030, GFF),
    UTT(2032, GFF),
    DDS(2033, BINARY),
    UTS(2035, GFF),
    LTR(2036, BINARY),
    _GFF(2037, GFF),
    FAC(2038, GFF),
    UTE(2040, GFF),
    UTD(2042, GFF),
    UTP(2044, GFF),
    DFT(2045, TEXT_INI),
    GIC(2046, GFF),
    GUI(2047, GFF),
    UTM(2051, GFF),
    DWK(2052, MDL_MODEL),
    PWK(2053, MDL_MODEL),
    JRL(2056, GFF),
    UTW(2058, GFF),
    SSF(2060, BINARY),
    NDB(2060, BINARY),
    PTM(2065, GFF),
    PTT(2066, GFF),
    LYT(3000, TEXT),
    MDX(3008, MDX_MODEL),
    BTC(2026, GFF),
    BTI(2024, GFF),
    VIS(3001, TEXT);

    private final int id;
    private final ContentType contentType;
    private static final Map<Integer, ResourceType> resourceTypeMap = new HashMap<>();

    static {
        for (ResourceType rt : values()) {
            resourceTypeMap.put(rt.getId(), rt);
        }
    }

    public static ResourceType getByType(int id) {
        return resourceTypeMap.get(id);
    }

    private ResourceType(int id, ContentType contentType) {
        this.id = id;
        this.contentType = contentType;
    }

    public int getId() {
        return id;
    }

    public ContentType getContentType() {
        return contentType;
    }

}
