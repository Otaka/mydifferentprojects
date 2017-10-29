package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class DoublePacker extends BasePacker {

    public DoublePacker(Quantificator quantificator) {
        super(quantificator);
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        long lv = readLong(stream);
        result.add((Double) Double.longBitsToDouble(lv));
    }

    public static long readLong(InputStream inputStream) throws IOException {
        long a1 = inputStream.read();
        long a2 = inputStream.read();
        long a3 = inputStream.read();
        long a4 = inputStream.read();
        long a5 = inputStream.read();
        long a6 = inputStream.read();
        long a7 = inputStream.read();
        long a8 = inputStream.read();
        return (a1 & 0xFF) | ((a2 & 0xFF) << 8) | ((a3 & 0xFF) << 16) | ((a4 & 0xFF) << 24)
                | ((a5 & 0xFF) << 32) | ((a6 & 0xFF) << 40) | ((a7 & 0xFF) << 48) | ((a8 & 0xFF) << 56);
    }

}
