package com.simplepl.intermediate.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author sad
 */
public class BytecodeGenerator {

    private ByteArrayOutputStream outputArray = new ByteArrayOutputStream(1000);

    public void genI8AssignVFromLiteral(String name, byte value) {
        
    }

    private void putByte(byte value) {
        outputArray.write(value & 0xFF);
    }

    private void putShort(short value) {
        try {
            outputArray.write(shortToByteArray(value));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void putInt(int value) {
        try {
            outputArray.write(intToByteArray(value));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void putLong(long value) {
        try {
            outputArray.write(longToByteArray(value));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void putFloat(float value) {
        try {
            outputArray.write(intToByteArray(Float.floatToRawIntBits(value)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void putDouble(double value) {
        try {
            outputArray.write(longToByteArray(Double.doubleToRawLongBits(value)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value};
    }

    public static final byte[] shortToByteArray(short value) {
        return new byte[]{
            (byte) (value >>> 8),
            (byte) value};
    }

    public static final byte[] longToByteArray(long value) {
        return new byte[]{
            (byte) (value >>> 56),
            (byte) (value >>> 48),
            (byte) (value >>> 40),
            (byte) (value >>> 32),
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value};
    }

}
