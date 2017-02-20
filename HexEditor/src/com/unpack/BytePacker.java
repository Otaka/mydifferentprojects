package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class BytePacker extends BasePacker {
    private final boolean unsigned;

    public BytePacker(Quantificator quantificator, boolean unsigned) {
        super(quantificator);
        this.unsigned = unsigned;
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        byte b = (byte) stream.read();
        result.add((short) (b & 0xFF));
    }

}
