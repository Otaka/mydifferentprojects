package com.nwn.data;

import static com.nwn.data.BaseReader.*;
import com.nwn.data.biff.BiffEntry;
import com.nwn.data.erf.Erf;
import com.nwn.data.key.KeyResource;
import com.nwn.data.key.ResourceType;
import java.io.*;

/**
 * @author Dmitry
 */
public class FileReaderErf extends BaseReader {
    public Erf loadFile(FileInputStream stream, File file) throws IOException {
        String magic = readString(stream, 4);
        if (!magic.equals("ERF ")) {
            throw new IllegalArgumentException("File " + file.getName() + " is not right BIFF file");
        }

        String version = readString(stream, 4);
        int languageCount = readInt(stream);
        int localizedStringSize = readInt(stream);
        int entryCount = readInt(stream);
        int offsetToLocalizedStrings = readInt(stream);
        int offsetToKeyList = readInt(stream);
        int offsetToResourcesList = readInt(stream);
        int buildYear = readInt(stream);
        int buildDay = readInt(stream);
        int descriptionStrRef = readInt(stream);

        setAbsolutePosition(stream, offsetToKeyList);
        KeyResource[] keyResources = loadKeyResources(stream, entryCount);
        Erf erf = new Erf();
        erf.setFile(file);
        erf.setKeyResources(keyResources);
        setAbsolutePosition(stream, offsetToResourcesList);
        BiffEntry[] resourceEntries = loadBiffEntries(stream, keyResources);
        erf.setResourceEntries(resourceEntries);
        return erf;
    }

    private KeyResource[] loadKeyResources(FileInputStream stream, int count) throws IOException {
        KeyResource[] resources = new KeyResource[count];
        for (int i = 0; i < count; i++) {
            String resRef = readString(stream, 16);
            int resourceId = readInt(stream);
            int resourceTypeId = readShort(stream);
            ResourceType resourceType = ResourceType.getByType(resourceTypeId);
            if (resourceType == null) {
                System.err.println("Cannot recognize type of the resource '" + resRef + "' with id = " + resourceTypeId);
            }
            skip(stream, 2);
            resources[i] = new KeyResource(resRef, resourceType, resourceId);
        }
        return resources;
    }

    private BiffEntry[] loadBiffEntries(FileInputStream stream, KeyResource[] keyResources) throws IOException {
        int numberOfEntries = keyResources.length;
        BiffEntry[] entries = new BiffEntry[numberOfEntries];
        for (int i = 0; i < numberOfEntries; i++) {
            int offset = readInt(stream);
            int size = readInt(stream);
            BiffEntry entry = new BiffEntry(keyResources[i], offset, size);
            entries[i] = entry;
        }

        return entries;
    }
}
