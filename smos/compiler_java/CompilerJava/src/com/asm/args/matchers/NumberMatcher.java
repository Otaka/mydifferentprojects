package com.asm.args.matchers;

import com.asm.args.argresult.AbstractParsingResult;
import com.asm.args.argresult.NumberResult;
import com.asm.exceptions.ParsingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**
 * @author sad
 */
public class NumberMatcher extends AbstractMatcher {

    private long min;
    private long max;
    private MemSize memSize;

    public NumberMatcher(MemSize memSize) {
        this(memSize, null);
    }

    public NumberMatcher(MemSize memSize, Boolean signed) {
        this.memSize = memSize;
        switch (memSize) {
            case BYTE:
                if (signed == null) {
                    min = -127;
                    max = 255;
                } else if (signed == true) {
                    min = -127;
                    max = 128;
                } else if (signed == false) {
                    min = 0;
                    max = 255;
                }
                break;
            case WORD:
                if (signed == null) {
                    min = Short.MIN_VALUE;
                    max = 65535;
                } else if (signed) {
                    min = Short.MIN_VALUE;
                    max = Short.MAX_VALUE;
                } else {
                    min = 0;
                    max = 65535;
                }
                break;
            case DWORD:
                if (signed == null) {
                    min = Integer.MIN_VALUE;
                    max = 4294967295l;
                } else if (signed) {
                    min = Integer.MIN_VALUE;
                    max = Integer.MAX_VALUE;
                } else {
                    min = 0;
                    max = 4294967295l;
                }
                break;
            default:
                throw new RuntimeException("Unimplemented MemorySize [" + memSize + "]");
        }
    }

    public long getMax() {
        return max;
    }

    public MemSize getMemSize() {
        return memSize;
    }

    public long getMin() {
        return min;
    }

    @Override
    public NumberResult match(String value) {
        long numValue = -1;
        try {
            if (value.startsWith("0x")) {
                numValue = parseHex(value);
            } else if (value.endsWith("b") || value.endsWith("B")) {
                numValue = parseBinary(value);
            } else if (value.startsWith("'") && value.endsWith("'")) {
                numValue = parseChar(value);
            } else {
                numValue = parseInt(value);
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        if (numValue >= min && numValue <= max) {
            return new NumberResult(memSize, numValue);
        }

        return null;
    }

    protected long parseHex(String value) {
        return Long.parseLong(value.substring(2), 16);
    }

    protected long parseInt(String value) {
        return Long.parseLong(value);
    }

    protected long parseBinary(String value) {
        return Long.parseLong(value.substring(0, value.length() - 1), 2);
    }

    /**
     Char/s will be automatically converted to the byte/word/dword
     */
    protected long parseChar(String value) {
        String data = value.substring(1, value.length() - 1);
        if (data.length() != memSize.dataSize) {
            throw new ParsingException("[" + value + "] chars cannot be used with " + memSize);
        }

        java.nio.charset.CharsetEncoder enc = Charset.forName("CP1251").newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        if (!enc.canEncode(data)) {
            throw new ParsingException("Character string [" + value + "] is not ASCII string");
        }

        byte[] bytes = data.getBytes();
        if (bytes.length != memSize.dataSize) {
            throw new ParsingException("[" + value + "] chars cannot be used with " + memSize);
        }

        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = (result << 8) | (bytes[i] & 0xFF);
        }

        return result;
    }

    public enum MemSize {

        BYTE(1), WORD(2), DWORD(4);
        private int dataSize;

        private MemSize(int dataSize) {
            this.dataSize = dataSize;
        }

    }
}
