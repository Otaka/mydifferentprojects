package com.kotorresearch.script.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class ByteArrayUtils {

    public static String getStringFromByteBuffer(byte[] buffer, int offset, int length) {
        return new String(buffer, offset, length);
    }

    public static boolean arrayContainsArray(byte[] buffer, int offset, byte[] arrayToCheck) {
        for (int i = 0; i < arrayToCheck.length; i++) {
            if (buffer[offset + i] != arrayToCheck[i]) {
                return false;
            }
        }

        return true;
    }

    public static String hex(int hex, int typeByteLength) {
        String resultString;
        if (typeByteLength == 1) {
            hex = hex & 0xFF;
        } else if (typeByteLength == 2) {
            hex = hex & 0xFFFF;
        } else if (typeByteLength == 4) {
            hex = hex & 0xFFFFFFFF;
        }

        resultString = Integer.toHexString(hex);
        resultString = resultString.toUpperCase();
        return StringUtils.leftPad(resultString, typeByteLength * 2, '0');
    }

    public static int getShortFromByteBuffer(byte[] buffer, int offset) {
        int result = 0;
        result = (buffer[offset] & 0xFF) << 8;
        result = result | ((buffer[offset + 1] & 0xFF));
        return result;
    }

    public static int getByteFromByteBuffer(byte[] buffer, int offset) {
        return buffer[offset] & 0xFF;
    }

    public static int getIntFromByteBuffer(byte[] buffer, int offset) {
        int result = 0;
        result = (buffer[offset] & 0xFF) << 24;
        result = result | ((buffer[offset + 1] & 0xFF) << 16);
        result = result | ((buffer[offset + 2] & 0xFF) << 8);
        result = result | (buffer[offset + 3] & 0xFF);
        return result;
    }

    public static void putIntToByteBuffer(byte[] buffer, int value, int offset) {
        buffer[offset + 3] = (byte) value;
        value = value >> 8;
        buffer[offset + 2] = (byte) value;
        value = value >> 8;
        buffer[offset + 1] = (byte) value;
        value = value >> 8;
        buffer[offset] = (byte) value;
    }

    public static void putShortToByteBuffer(byte[] buffer, int value, int offset) {
        buffer[offset + 1] = (byte) value;
        value = value >> 8;
        buffer[offset] = (byte) value;
    }

    public static String printBuffer(byte[] array, int offset, int length, boolean putSpacesBetweenBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte b = array[i + offset];
            String v = Integer.toHexString(b & 0xFF).toUpperCase();
            if (v.length() == 1) {
                v = "0" + v;
            }
            sb.append(v);
            if (putSpacesBetweenBytes) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
