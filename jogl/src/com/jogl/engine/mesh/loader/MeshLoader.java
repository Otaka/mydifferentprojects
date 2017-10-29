package com.jogl.engine.mesh.loader;

import com.jogl.engine.SceneManager;
import com.jogl.engine.node.Node;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.io.*;

/**
 * @author Dmitry
 */
public abstract class MeshLoader {
    public abstract String[] getExtensions();

    public abstract boolean isMatch(JoglFileInputStream stream);

    public abstract Node load(SceneManager sceneManager, File file, JoglFileInputStream stream) throws IOException;

    protected static final StringBuilder tempBuffer = new StringBuilder();
    private long baseFileOffset;

    protected void init(FileInputStream stream) throws IOException {
        baseFileOffset = stream.getChannel().position();
    }

    /*public static String readTabEndedString(InputStream stream) throws IOException {
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

     public static void skipRead(InputStream stream, int count) throws IOException {
     stream.skip(count);
     }

     public static int readInt(InputStream inputStream) throws IOException {
     return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8) | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24);
     }

     public static float[] readVector3(InputStream inputStream) throws IOException {
     return readFloatArray(inputStream, 3);
     }

     public static float[] readFloatArray(InputStream inputStream, int size) throws IOException {
     float[] vector = new float[size];
     for (int i = 0; i < size; i++) {
     vector[i] = readFloat(inputStream);
     }
     return vector;
     }

     public static float readFloat(InputStream inputStream) throws IOException {
     return Float.intBitsToFloat(readInt(inputStream));
     }

     public static long readLong(InputStream inputStream) throws IOException {
     return (inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8)
     | ((inputStream.read() & 0xFF) << 16) | ((inputStream.read() & 0xFF) << 24)
     | ((inputStream.read() & 0xFF) << 32) | ((inputStream.read() & 0xFF) << 40)
     | ((inputStream.read() & 0xFF) << 48) | ((inputStream.read() & 0xFF) << 56);
     }

     public static short readShort(InputStream inputStream) throws IOException {
     return (short) ((inputStream.read() & 0xFF) | ((inputStream.read() & 0xFF) << 8));
     }*/
    /* public ArraySizeAndOffset readArraySizeAndOffset(JoglFileInputStream inputStream) throws IOException {
     int offset = inputStream.readInt();
     int usedCount = inputStream.readInt();
     inputStream.skip(4);
     return new ArraySizeAndOffset(offset, usedCount);
     }

     protected class ArraySizeAndOffset {
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

     }*/
}
