package com.nwn;

import com.nwn.biff.Biff;
import com.nwn.biff.BiffEntry;
import com.nwn.key.Key;
import com.nwn.key.KeyResource;
import java.io.*;

/**
 * @author Dmitry
 */
public class FileReaderBiff extends BaseReader {
    public Biff loadFile(FileInputStream stream, File file, Key keyFile) throws IOException {
        String magic = readString(stream, 4);
        if (!magic.equals("BIFF")) {
            throw new IllegalArgumentException("File " + file.getName() + " is not right BIFF file");
        }

        String version = readString(stream, 4);
        int variableResourcesCount = readInt(stream);
        int fixedResourcesCount = readInt(stream);
        int variableTableOffset = readInt(stream);
        setAbsolutePosition(stream, variableTableOffset);
        BiffEntry[] entries = loadBiffDescription(keyFile, stream, variableResourcesCount);
        return new Biff(entries, file);
    }

    private BiffEntry[] loadBiffDescription(Key key, FileInputStream stream, int count) throws IOException {
        BiffEntry[] biffEntries = new BiffEntry[count];
        for (int i = 0; i < count; i++) {
            int id = readInt(stream);
            int offset = readInt(stream);
            int fileSize = readInt(stream);
            int resourceType = readInt(stream);
            KeyResource kr = key.getResourceByResId(id);
            BiffEntry biffEntry = new BiffEntry(kr, offset, fileSize);
            biffEntries[i] = biffEntry;
        }
        return biffEntries;
    }
}
