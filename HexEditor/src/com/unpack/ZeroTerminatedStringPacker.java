package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Dmitry
 */
class ZeroTerminatedStringPacker extends BasePacker {
    private int maxSize;

    public ZeroTerminatedStringPacker(int maxSize) {
        super(null);
        this.maxSize = maxSize;
    }

    @Override
    void process(InputStream stream, List<Object> result) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (maxSize == -1) {
            char c = (char) stream.read();
            while (c != -1 && c != 0) {
                sb.append(c);
                c = (char) stream.read();
            }
        } else {
            boolean isStringFinished = false;
            for (int i = 0; i < maxSize; i++) {
                char c = (char) stream.read();
                if (c == 0) {
                    isStringFinished = true;
                }
                if (!isStringFinished) {
                    sb.append(c);
                }
            }
        }

        result.add(sb.toString());
    }

    @Override
    protected void innerProcess(InputStream stream, List<Object> result) {

    }
}
