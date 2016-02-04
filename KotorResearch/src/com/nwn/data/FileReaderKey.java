package com.nwn.data;

import com.nwn.data.key.KeyResource;
import com.nwn.data.key.ResourceType;
import com.nwn.data.key.Key;
import com.nwn.data.key.BifShort;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class FileReaderKey extends BaseReader {
    public Key loadFile(FileInputStream stream, String fileName) throws IOException {
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
        setAbsolutePosition(stream, offsetToKeyTable);
        KeyResource[] keyResources = readKeyTable(stream, keyCount);
        setAbsolutePosition(stream, offsetToFileTable);
        BifShort[] biffs = readBiffs(stream, bifCount);
        return new Key(keyResources, biffs);
    }

    private BifShort[] readBiffs(FileInputStream stream, int biffCount) throws IOException {
        BifShort[] biffs = new BifShort[biffCount];
        for (int i = 0; i < biffCount; i++) {
            int fileSize = readInt(stream);
            int fileNameOffset = readInt(stream);
            int fileNameSize = readShort(stream);
            int drive = readShort(stream);
            long pos = stream.getChannel().position();
            setAbsolutePosition(stream, fileNameOffset);
            String name = readString(stream, fileNameSize);
            stream.getChannel().position(pos);
            biffs[i] = new BifShort(name, fileSize);
        }
        return biffs;
    }

    private KeyResource[] readKeyTable(FileInputStream stream, int keysCount) throws IOException {
        KeyResource[] keyResources = new KeyResource[keysCount];
        for (int i = 0; i < keysCount; i++) {
            String resRef = readString(stream, 16);
            int resourceTypeId = readShort(stream);
            ResourceType resourceType = ResourceType.getByType(resourceTypeId);
            if (resourceType == null) {
                System.err.println("Cannot recognize type of the resource '" + resRef + "' with id = " + resourceTypeId);
            }
            int resId = readInt(stream);
            keyResources[i] = new KeyResource(resRef, resourceType, resId);
        }
        return keyResources;
    }
}
