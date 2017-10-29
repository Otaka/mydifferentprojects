package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class SkipPacker extends BasePacker {
    public SkipPacker(Quantificator quantificator) {
        super(quantificator);
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) throws IOException {
        stream.read();
    }
}
