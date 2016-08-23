package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dmitry
 */
class GreedyQuantificator extends Quantificator {

    @Override
    public boolean check(InputStream stream, int index) throws IOException {
        stream.mark(1);
        int c = stream.read();
        if (c == -1) {
            return false;
        }

        stream.reset();
        return true;
    }

}
