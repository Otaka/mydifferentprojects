package com.nwn.dialog;

import com.nwn.BaseReader;
import com.nwn.NwnByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReaderTlk extends BaseReader {

    public FileReaderTlk() {
    }

    public Tlk loadFile(FileInputStream stream, String fileName) throws IOException {
        init(stream);

        String type = readString(stream, 4);
        if (!type.equals("TLK ")) {
            throw new IllegalArgumentException("File " + fileName + " is not a valid tlk file");
        }

        String version = readString(stream, 4);
        int languageId = readInt(stream);
        int stringCount = readInt(stream);
        int stringEntriesOffset = readInt(stream);
        long position = stream.getChannel().position();
        int rawDataSize = (int) (stream.getChannel().size() - stringEntriesOffset);
        setAbsolutePosition(stream, stringEntriesOffset);
        byte[] rawDataBuffer = new byte[rawDataSize];
        NwnByteArrayInputStream rawDataInputStream = new NwnByteArrayInputStream(rawDataBuffer);

        stream.read(rawDataBuffer);
        setAbsolutePosition(stream, (int) position);
        StringEntry[] entries = loadStringDataTable(stream, stringCount, rawDataInputStream);
        Tlk tlk = new Tlk(languageId, entries);
        return tlk;
    }

    private StringEntry[] loadStringDataTable(FileInputStream stream, int stringCount, NwnByteArrayInputStream rawDataInputStream) throws IOException {
        StringEntry[] strings = new StringEntry[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int stringRef = i;
            int flags = readInt(stream);
            String soundResRef = readString(stream, 16);
            int volumeVariance = readInt(stream);
            int pitchVariance = readInt(stream);
            int offsetToString = readInt(stream);
            int stringSize = readInt(stream);
            float soundLength = Float.intBitsToFloat(readInt(stream));
            rawDataInputStream.setPosition(offsetToString);
            String stringValue = readString(rawDataInputStream, stringSize);
            StringEntry string = new StringEntry(stringRef, stringValue, soundResRef, soundLength, flags);
            strings[i] = string;
        }
        return strings;
    }
}
