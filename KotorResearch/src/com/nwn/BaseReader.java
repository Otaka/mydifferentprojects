package nwn;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sad
 */
public class BaseReader {

    protected static final StringBuilder tempBuffer = new StringBuilder();
    private long baseFileOffset;

    protected void init(FileInputStream stream) throws IOException {
        baseFileOffset = stream.getChannel().position();
    }

    protected void setAbsolutePosition(FileInputStream stream, int pos) throws IOException {
        stream.getChannel().position(baseFileOffset + pos);
    }

    protected void setRelativePosition(FileInputStream stream, int pos) throws IOException {
        long position = stream.getChannel().position();
        stream.getChannel().position(position + pos);
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
    
    public static long readLong(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24)|
                ((inputStream.read() & 0xFF)<<32) | ((inputStream.read() & 0xFF) << 40) | ((inputStream.read() & 0xFF) << 48) | ((inputStream.read() & 0xFF) << 56);
    }

    public static short readShort(InputStream inputStream) throws IOException {
        return (short) ((inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8));
    }
}
