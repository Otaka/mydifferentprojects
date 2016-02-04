package com.nwn.data.tpc;

import com.nwn.exceptions.ParsingException;

/**
 * @author Dmitry
 */
public enum TpcEncoding {
    GRAY(1), RGB(2), RGBA(4), PACKED_BGRA(0xC);
    private final int encoding;

    private TpcEncoding(int encoding) {
        this.encoding = encoding;
    }

    public int getEncodingValue() {
        return encoding;
    }

    public static TpcEncoding fromValue(int value) {
        switch (value) {
            case 1:
                return GRAY;
            case 2:
                return RGB;
            case 4:
                return RGBA;
            case 0x0C:
                return PACKED_BGRA;
            default:
                throw new ParsingException("Cannot parse tpc encoding " + value + ". Should be only [1,2,3,12]");

        }
    }
}
