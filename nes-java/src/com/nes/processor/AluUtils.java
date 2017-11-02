package com.nes.processor;

/**
 * @author sad
 */
public class AluUtils {

    public static byte setBit(byte value, int bitNumber, boolean state) {
        if (bitNumber > 7 || bitNumber < 0) {
            throw new IllegalArgumentException("Cannot set bit " + bitNumber + " in byte");
        }
        if (state == true) {
            byte flag = (byte) ((byte) 1 << (byte) bitNumber);
            value = (byte) (value | flag);
            return value;
        } else {
            byte flag = (byte) ~(byte) ((byte) 1 << (byte) bitNumber);
            value = (byte) (value & flag);
            return value;
        }
    }

    public static boolean isBit(byte value, int bitNumber) {
        if (bitNumber > 7 || bitNumber < 0) {
            throw new IllegalArgumentException("Cannot set bit " + bitNumber + " in byte");
        }
        return (value & (1 << bitNumber)) != 0;
    }

    public static int unsignedByte(byte value) {
        return value & 0xff;
    }

    public static int lhTo16Bit(byte l, byte h) {
        return (AluUtils.unsignedByte(h) << 8) | AluUtils.unsignedByte(l);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (byte b : bytes) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(String.format("%02x", b & 0xff));
            first = false;
        }
        return sb.toString();
    }

    public static String byteToHexString(byte b) {
        return String.format("%02x", b & 0xff);
    }
    
    public static byte[] hexStringToByteArray(String s) {
        s=s.replaceAll(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static String i16ToHexString(int b) {
        return String.format("%02x", (b>>8) & 0xff)+String.format("%02x", b & 0xff);
    }
    
    public static void assertBytesEquals(byte[] expected, byte[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if ((expected != null && actual == null) || (expected == null && actual != null)) {
            String message="Expected " + expected == null ? "null" : "not null" + " but found " + actual == null ? "null" : "not null";
            throw new RuntimeException(message);
        }
        if (expected.length != actual.length) {
            String message="Expected size of array =" + expected.length + " but actual=" + actual.length;
            throw new RuntimeException(message);
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                String message="Failed test. Expected [" + i + "]=" + expected[i] + ". Found [" + i + "]=" + actual[i];
                throw new RuntimeException(message);
            }
        }
    }
}
