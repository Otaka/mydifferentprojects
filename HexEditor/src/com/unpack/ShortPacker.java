package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class ShortPacker extends BasePacker {
    private final boolean unsigned;

    public ShortPacker(Quantificator quantificator, boolean unsigned) {
        super(quantificator);
        this.unsigned = unsigned;
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        short sv = readShort(stream);
        result.add((Short) sv);
    }

    public static short readShort(InputStream inputStream) throws IOException {
        return (short) ((inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8));
    }
}
