package com.jogl.engine.utils.io;

import java.io.*;
import java.util.ArrayList;

public class JoglFileInputStream extends FileInputStream {
    protected static final StringBuilder tempBuffer = new StringBuilder();
    private long baseFileOffset;
    private String currentLabel = null;
    private int currentLabelIndex = -1;
    private final ArrayList<String> labelTable = new ArrayList<>();
    private PrintStream parserDescriptionOutStream;
    private final byte[] temp4byteArray = new byte[4];

    private final static int INT = 0;
    private final static int STRING_FIXED = 1;
    private final static int FLOAT = 2;
    private final static int STRING_NULL_TERMINATED = 3;
    private final static int SKIP = 4;
    private final static int SHORT = 5;
    private final static int LONG = 6;
    private final static int BYTE = 7;
    private File descriptionFile;

    public void setCurrentLabel(String currentLabel) {
        if (parserDescriptionOutStream != null) {
            this.currentLabel = currentLabel;
            currentLabelIndex = getOrCreateLabelIndex(currentLabel);
        }
    }

    public void storeFileParsingDescriptionInfo(File file) throws FileNotFoundException, IOException {
        descriptionFile = file;
        file.delete();
        parserDescriptionOutStream = new PrintStream(file);
        parserDescriptionOutStream.write(intToByteArray(0));
    }

    public void closeFileParsingDescriptionInfo() throws FileNotFoundException, IOException {
        parserDescriptionOutStream.close();
        int offsetToStringTable = (int) descriptionFile.length();
        try (RandomAccessFile stream = new RandomAccessFile(descriptionFile, "rw")) {
            stream.seek(offsetToStringTable);
            for (int i = 0; i < labelTable.size(); i++) {
                String label = labelTable.get(i);
                stream.writeInt(label.length());
                stream.write(label.getBytes());
            }
            stream.seek(0);
            stream.writeInt(offsetToStringTable);
        }
    }

    private int getOrCreateLabelIndex(String label) {
        for (int i = 0; i < labelTable.size(); i++) {
            if (labelTable.get(i).equals(label)) {
                return i;
            }
        }

        labelTable.add(label);
        return labelTable.size() - 1;
    }

    private void saveParsingChunk(int offset, int type, int size) throws IOException {
        if (parserDescriptionOutStream != null) {
            parserDescriptionOutStream.write(intToByteArray(currentLabelIndex));
            parserDescriptionOutStream.write(intToByteArray(offset));
            parserDescriptionOutStream.write(intToByteArray(size));
            parserDescriptionOutStream.write(intToByteArray(type));
        }
    }

    private void saveParsingChunk(int type, int size) throws IOException {
        if (parserDescriptionOutStream != null) {
            int offset = (int) getChannel().position();
            saveParsingChunk(offset, type, size);
        }
    }

    public byte[] intToByteArray(int value) {
        temp4byteArray[0] = (byte) (value >>> 24);
        temp4byteArray[1] = (byte) (value >>> 16);
        temp4byteArray[2] = (byte) (value >>> 8);
        temp4byteArray[3] = (byte) (value);
        return temp4byteArray;
    }

    public JoglFileInputStream(File file) throws FileNotFoundException {
        super(file);
    }

    public void rememberFileOffset() throws IOException {
        baseFileOffset = getChannel().position();
    }

    public int getAbsolutePosition() throws IOException {
        return (int) getChannel().position();
    }

    public void setAbsolutePosition(int pos) throws IOException {
        getChannel().position(baseFileOffset + pos);
    }

    public void setRelativePosition(int pos) throws IOException {
        long position = getChannel().position();
        getChannel().position(position + pos);
    }

    public String readTabEndedString() throws IOException {
        while (true) {
            int value = read();
            if (value == 0) {
                return null;
            }
            if (value == '\t') {
                String result = tempBuffer.toString();
                tempBuffer.setLength(0);
                return result;
            }

            tempBuffer.append((char) value);
        }
    }

    public String readNullTerminatedString(String label) throws IOException {
        setCurrentLabel(label);
        return readNullTerminatedString();
    }

    public String readNullTerminatedString() throws IOException {
        int offset = 0;
        if (parserDescriptionOutStream != null) {
            offset = (int) getChannel().position();
        }

        int readCount = 0;
        while (true) {
            int value = read();
            readCount++;
            if (value == -1) {
                tempBuffer.setLength(0);
                return null;
            }

            if (value == 0) {
                String result = tempBuffer.toString();
                tempBuffer.setLength(0);
                saveParsingChunk(offset, STRING_NULL_TERMINATED, readCount);
                return result;
            }

            tempBuffer.append((char) value);
        }
    }

    public void readBytes(byte[] buffer, String label) throws IOException {
        setCurrentLabel(label);
        saveParsingChunk(BYTE, buffer.length);
        read(buffer);
    }

    public void readBytes(byte[] buffer) throws IOException {
        read(buffer);
    }

    public String readString(int count, String label) throws IOException {
        setCurrentLabel(label);
        return readString(count);
    }

    public String readString(int count) throws IOException {
        saveParsingChunk(STRING_FIXED, count);
        for (int i = 0; i < count; i++) {
            int value = read();
            if (value == 0) {

            } else {
                tempBuffer.append((char) value);
            }

        }
        String result = tempBuffer.toString();
        tempBuffer.setLength(0);

        return result;
    }

    public int readInt(String label) throws IOException {
        setCurrentLabel(label);
        saveParsingChunk(INT, 4);
        return (read() & 0xFF) | ((read() & 0xFF) << 8) | ((read() & 0xFF) << 16) | ((read() & 0xFF) << 24);
    }

    public int readInt() throws IOException {
        saveParsingChunk(INT, 4);
        return (read() & 0xFF) | ((read() & 0xFF) << 8) | ((read() & 0xFF) << 16) | ((read() & 0xFF) << 24);
    }

    public float[] readVector3() throws IOException {
        return readFloatArray(3);
    }

    public float[] readFloatArray(int size) throws IOException {
        float[] vector = new float[size];
        for (int i = 0; i < size; i++) {
            vector[i] = readFloat();
        }
        return vector;
    }

    public float readFloat(String label) throws IOException {
        setCurrentLabel(label);
        return readFloat();
    }

    public float readFloat() throws IOException {
        saveParsingChunk(FLOAT, 4);
        int value = (read() & 0xFF) | ((read() & 0xFF) << 8) | ((read() & 0xFF) << 16) | ((read() & 0xFF) << 24);
        return Float.intBitsToFloat(value);
    }

    public long readLong(String label) throws IOException {
        setCurrentLabel(label);
        return readLong();
    }

    public long readLong() throws IOException {
        saveParsingChunk(LONG, 8);
        return (read() & 0xFF) | ((read() & 0xFF) << 8)
                | ((read() & 0xFF) << 16) | ((read() & 0xFF) << 24)
                | ((((long) read() & 0xFF)) << 32) | ((((long) read() & 0xFF)) << 40)
                | ((((long) read() & 0xFF)) << 48) | ((((long) read() & 0xFF)) << 56);
    }

    public short readShort(String label) throws IOException {
        setCurrentLabel(label);
        return readShort();
    }

    public short readShort() throws IOException {
        saveParsingChunk(SHORT, 2);
        getAbsolutePosition();
        return (short) ((read() & 0xFF) | ((read() & 0xFF) << 8));
    }

    public int readByte(String label) throws IOException {
        setCurrentLabel(label);
        return readByte();
    }

    public int readByte() throws IOException {
        saveParsingChunk(BYTE, 1);
        return read();
    }

    public ArraySizeAndOffset readArraySizeAndOffset() throws IOException {
        int offset = readInt();
        int usedCount = readInt();
        skipBytes(4);
        return new ArraySizeAndOffset(offset, usedCount);
    }

    public ArraySizeAndOffset readArraySizeAndOffset(String label) throws IOException {
        int offset = readInt(label + "ArrayOffset");
        int usedCount = readInt(label + "ArrayCount");
        skipBytes(4, label + "ArrayOffsetCountSkip");
        return new ArraySizeAndOffset(offset, usedCount);
    }

    public void skipBytes(int count, String label) throws IOException {
        setCurrentLabel(label);
        skipBytes(count);
    }

    public void skipBytes(int count) throws IOException {
        saveParsingChunk(SKIP, count);
        skip(count);
    }

    public class ArraySizeAndOffset {
        private final int offset;
        private final int count;

        public ArraySizeAndOffset(int offset, int count) {
            this.offset = offset;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return "Count=" + count + " Offset=" + offset;
        }
    }
}
