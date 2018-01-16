package com.nwn.data;

import java.io.*;
import java.lang.reflect.Field;

/**
 * @author sad
 */
public class BaseReader {

    protected static final StringBuilder tempBuffer = new StringBuilder();
    private long baseFileOffset;

    private static Field posField;

    static {
        try {
            posField = ByteArrayInputStream.class.getDeclaredField("pos");
            posField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void init(FileInputStream stream) throws IOException {
        baseFileOffset = stream.getChannel().position();
    }

    protected void setAbsolutePosition(FileInputStream stream, int pos) throws IOException {
        stream.getChannel().position(baseFileOffset + pos);
    }

    protected void setAbsolutePosition(ByteArrayInputStream stream, int pos) throws IOException {
        stream.reset();
        stream.skip(pos);
    }

    protected long getPosition(FileInputStream stream) throws IOException {
        long position = stream.getChannel().position() - baseFileOffset;
        return position;
    }

    protected long getPosition(ByteArrayInputStream stream) throws IOException {
        try {
            return posField.getInt(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    protected void setRelativePosition(FileInputStream stream, int pos) throws IOException {
        long position = stream.getChannel().position();
        stream.getChannel().position(position + pos);
    }

    protected void setRelativePosition(ByteArrayInputStream stream, int pos) throws IOException {
        long position = getPosition(stream);
        setAbsolutePosition(stream, (int) (position + pos));
    }

    public static String readTabEndedString(InputStream stream) throws IOException {
        while (true) {
            int value = stream.read();
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

    public static String readNullTerminatedString(InputStream stream) throws IOException {
        while (true) {
            int value = stream.read();
            if (value == -1) {
                tempBuffer.setLength(0);
                return null;
            }
            if (value == 0) {
                String result = tempBuffer.toString();
                tempBuffer.setLength(0);
                return result;
            }

            tempBuffer.append((char) value);
        }
    }

    public static String readString(InputStream stream, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            int value = stream.read();
            if (value == 0) {

            } else {
                tempBuffer.append((char) value);
            }

        }
        String result = tempBuffer.toString();
        tempBuffer.setLength(0);
        return result;
    }

    public static int readInt(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24);
    }

    public static int readByte(InputStream inputStream) throws IOException {
        return inputStream.read() & 0xFF;
    }

    public static float readFloat(InputStream inputStream) throws IOException {
        return Float.intBitsToFloat(readInt(inputStream));
    }

    public static void skip(InputStream inputStream, int count) throws IOException {
        inputStream.skip(count);
    }

    public static long readLong(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xFF) | (((long)inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24)
                | (((long)inputStream.read() & 0xFF) << 32) | (((long)inputStream.read() & 0xFF) << 40) | (((long)inputStream.read() & 0xFF) << 48) | (((long)inputStream.read() & 0xFF) << 56);
    }

    public static short readShort(InputStream inputStream) throws IOException {
        return (short) ((inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8));
    }
}
