package com.nwn.data;

import com.nwn.data.script.NwScript;
import java.io.*;

/**
 * @author Dmitry
 */
public class FileReaderNcs extends BaseReader {

    public FileReaderNcs() {
    }

    public NwScript loadFile(FileInputStream stream, String fileName) throws IOException {
        init(stream);
        String magic = readString(stream, 4);
        if (magic.equals("NCS ")) {
            throw new IllegalArgumentException("File " + fileName + " is not a valid compiled Nwn script");
        }

        String version = readString(stream, 4);
        int size = readInt(stream);
        size -= 12;
        byte[] buffer = new byte[size];
        stream.read(buffer);
        NwScript script = new NwScript(buffer, fileName);
        return script;
    }
}
