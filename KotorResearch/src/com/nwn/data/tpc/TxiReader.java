package com.nwn.data.tpc;

import com.nwn.data.BaseReader;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class TxiReader extends BaseReader {
    private int size;

    public TxiReader(FileInputStream stream, int size) throws IOException {
        init(stream);
        this.size = size;
    }

    public Txi load() {
        return null;
    }
}
