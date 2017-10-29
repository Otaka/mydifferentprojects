package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dmitry
 */
abstract class Quantificator {
    public abstract boolean check(InputStream inputstream, int index) throws IOException;
}
