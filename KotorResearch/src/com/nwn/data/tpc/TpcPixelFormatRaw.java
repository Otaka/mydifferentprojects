package com.nwn.data.tpc;

import com.nwn.exceptions.ParsingException;

/**
 * @author Dmitry
 */
public enum TpcPixelFormatRaw {
    RGBA8(32856), RGB8(32849), DXT1(33776), DXT3(33778), DXT5(33779);
    private final int encoding;

    private TpcPixelFormatRaw(int encoding) {
        this.encoding = encoding;
    }

    public int getEncodingValue() {
        return encoding;
    }

    public static TpcPixelFormatRaw fromValue(int value) {
        switch (value) {
            case 32856:
                return RGBA8;
            case 32849:
                return RGB8;
            case 33776:
                return DXT1;
            case 33778:
                return DXT3;
            case 33779:
                return DXT5;
            default:
                throw new ParsingException("Cannot parse TpcPixelFormatRaw " + value + ".");

        }
    }
}
