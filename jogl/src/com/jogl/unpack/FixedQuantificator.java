package com.jogl.unpack;

import java.io.InputStream;

/**
 * @author Dmitry
 */
class FixedQuantificator extends Quantificator {
    private int max;

    public FixedQuantificator(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean check(InputStream stream, int index) {
        return index < max;
    }

}
