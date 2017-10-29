package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class FloatPacker extends BasePacker {

    public FloatPacker(Quantificator quantificator) {
        super(quantificator);
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        int intValue = readInt(stream);
        result.add((Float) Float.intBitsToFloat(intValue));
    }

    public static int readInt(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24);
    }

}
