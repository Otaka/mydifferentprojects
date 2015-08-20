package com.nwn.dialog;

import com.nwn.BaseReader;
import com.nwn.NwnByteArrayInputStream;
import java.io.*;
import java.nio.charset.Charset;

public class FileReaderTlk extends BaseReader {
    private final Charset charset;

    public FileReaderTlk() {
        charset = Charset.forName("cp1251");
    }

    public Tlk loadFile(FileInputStream fileStream, String fileName) throws IOException {
        init(fileStream);
        long size = fileStream.getChannel().size();
        byte[] fullBuffer = new byte[(int) size];
        fileStream.read(fullBuffer);
        fileStream.close();
        NwnByteArrayInputStream stream = new NwnByteArrayInputStream(fullBuffer);
        String type = readString(stream, 4);
        if (!type.equals("TLK ")) {
            throw new IllegalArgumentException("File " + fileName + " is not a valid tlk file");
        }

        String version = readString(stream, 4);
        int languageId = readInt(stream);
        int stringCount = readInt(stream);
        int stringEntriesOffset = readInt(stream);
        long position = stream.getPosition();
        int rawDataSize = (int) (size - stringEntriesOffset);
        stream.setPosition(stringEntriesOffset);
        //setAbsolutePosition(stream, stringEntriesOffset);
        byte[] rawDataBuffer = new byte[rawDataSize];
        NwnByteArrayInputStream rawDataInputStream = new NwnByteArrayInputStream(rawDataBuffer);

        stream.read(rawDataBuffer);

        stream.setPosition((int) position);
        //setAbsolutePosition(stream, (int) position);
        StringEntry[] entries = loadStringDataTable(stream, stringCount, rawDataInputStream);
        Tlk tlk = new Tlk(languageId, entries);
        return tlk;
    }

    private StringEntry[] loadStringDataTable(NwnByteArrayInputStream stream, int stringCount, NwnByteArrayInputStream rawDataInputStream) throws IOException {
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
            stringValue = new String(stringValue.getBytes("cp1252"), charset);
            StringEntry string = new StringEntry(stringRef, stringValue, soundResRef, soundLength, flags);
            strings[i] = string;
        }
        return strings;
    }
}
