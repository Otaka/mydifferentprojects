package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class ZCharPacker extends BasePacker {

    public ZCharPacker() {
        super(null);
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        char value = (char) stream.read();
        result.add((Character) value);
    }
}
