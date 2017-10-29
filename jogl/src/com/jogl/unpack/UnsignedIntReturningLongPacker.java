package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class UnsignedIntReturningLongPacker extends BasePacker {

    public UnsignedIntReturningLongPacker(Quantificator quantificator) {
        super(quantificator);
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        long value = readInt(stream);
        result.add(value);

    }

    public static long readInt(InputStream inputStream) throws IOException {
        return ((inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24)) & 0xffffffffl;
    }

}
