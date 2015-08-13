package com.nwn;

import com.nwn.biff.Biff;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class FileReaderKey extends BaseReader {
    public Biff loadFile(FileInputStream stream, String fileName) throws IOException {
        Biff biff = new Biff();
        String magic = readString(stream, 4);
        if (!magic.equals("KEY ")) {
            throw new IllegalArgumentException("File " + fileName + " is not right KEY file");
        }

        String version = readString(stream, 4);

        int bifCount = readInt(stream);
        int keyCount = readInt(stream);
        int offsetToFileTable = readInt(stream);
        int offsetToKeyTable = readInt(stream);
        int buildYear = readInt(stream);
        int buildDay = readInt(stream);
        stream.skip(32);//reserved

        return biff;
    }
}
