package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
public abstract class BasePacker {
    final Quantificator quantificator;

    BasePacker(Quantificator quantificator) {
        this.quantificator = quantificator;
    }

    void process(InputStream stream, List<Object> result) throws IOException {
        if (quantificator == null) {
            innerProcess(stream, result);
        } else {
            try {
                int i = 0;
                while (quantificator.check(stream, i)) {
                    innerProcess(stream, result);
                    i++;
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected abstract void innerProcess(InputStream stream, List<Object> result) throws IOException;
}
