package com.nwn;

import com.nwn.biff.Biff;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class FileReaderBiff extends BaseReader {
    public Biff loadFile(FileInputStream stream, String fileName) throws IOException {
        Biff biff = new Biff();
        String magic = readString(stream, 4);
        if (!magic.equals("BIFF")) {
            throw new IllegalArgumentException("File " + fileName + " is not right BIFF file");
        }

        String version = readString(stream, 4);
        int variableResourcesCount = readInt(stream);
        int fixedResourcesCount = readInt(stream);
        int variableTableOffset = readInt(stream);

        return biff;
    }
}
