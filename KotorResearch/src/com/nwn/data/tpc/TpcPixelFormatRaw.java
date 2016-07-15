package com.nwn.data.tpc;

import com.nwn.exceptions.ParsingException;

/**
 * @author Dmitry
 */
public enum TpcPixelFormatRaw {
    GRAY(3), RGBA8(4), RGB8(5), RGB5A1(6), RGB5(7), DXT1(8), DXT3(9), DXT5(10);
    private final int encoding;

    private TpcPixelFormatRaw(int encoding) {
        this.encoding = encoding;
    }

    public int getEncodingValue() {
        return encoding;
    }

    public static TpcPixelFormatRaw fromValue(int value) {
        for (TpcPixelFormatRaw format : TpcPixelFormatRaw.values()) {
            if (format.getEncodingValue() == value) {
                return format;
            }
        }

        throw new ParsingException("Cannot parse TpcPixelFormatRaw " + value + ".");
    }
}
