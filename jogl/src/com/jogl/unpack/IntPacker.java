package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class IntPacker extends BasePacker {
    private final boolean unsigned;

    public IntPacker(Quantificator quantificator, boolean unsigned) {
        super(quantificator);
        this.unsigned = unsigned;
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        int value = readInt(stream);
        result.add(value);

    }

    public static int readInt(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24);
    }

}
