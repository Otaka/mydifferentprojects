package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;
import com.asm.args.parser.results.ImmediateResult;
import com.asm.exceptions.ParsingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**
 * @author sad
 */
public class NumberParser extends AbstractParser {

    private long min;
    private long max;
    private MemSize memSize;

    public NumberParser(MemSize memSize) {
        this(memSize, null);
    }

    public NumberParser(MemSize memSize, Boolean signed) {
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

    protected static long parseHex(String value) {
        return Long.parseLong(value.substring(2), 16);
    }

    protected static long parseInt(String value) {
        return Long.parseLong(value);
    }

    protected static long parseBinary(String value) {
        return Long.parseLong(value.substring(0, value.length() - 1), 2);
    }

    /**
     Char/s will be automatically converted to the byte/word/dword
     */
    protected static long parseChar(String value, int currentDataSize) {
        String data = value.substring(1, value.length() - 1);
        if (data.length() != currentDataSize) {
            throw new ParsingException("[" + value + "] chars cannot be used with " + currentDataSize);
        }

        java.nio.charset.CharsetEncoder enc = Charset.forName("CP1251").newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        if (!enc.canEncode(data)) {
            throw new ParsingException("Character string [" + value + "] is not ASCII string");
        }

        byte[] bytes = data.getBytes();
        if (bytes.length != currentDataSize) {
            throw new ParsingException("[" + value + "] chars cannot be used with " + currentDataSize);
        }

        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = (result << 8) | (bytes[i] & 0xFF);
        }

        return result;
    }

    /**
     Parsing number in following formats<br/>
     65656 - decimal<br/>
     0xAABBCC - hex<br/>
     01001011b - binary<br/>
     'CAFE' - every char is a byte<br/>
     In case of error - throws an NumberFormatException
    
     */
    public static long tryToParseNumber(String value, int currentDataSize) {
        long numValue = -1;
        if (value.startsWith("0x")) {
            numValue = parseHex(value);
        } else if (value.endsWith("b") || value.endsWith("B")) {
            numValue = parseBinary(value);
        } else if (value.startsWith("'") && value.endsWith("'")) {
            numValue = parseChar(value, currentDataSize);
        } else {
            numValue = parseInt(value);
        }
        return numValue;
    }

    @Override
    public AbstractParseResult parse(String value, Context context) {
        long numValue = -1;
        try {
            numValue = tryToParseNumber(value, memSize.dataSize);
        } catch (NumberFormatException ex) {
            return null;
        }

        if (checkLimits(numValue)) {
            return new ImmediateResult(numValue, memSize);
        }

        return null;
    }

    public boolean checkLimits(long numValue) {
        if (numValue >= min && numValue <= max) {
            return true;
        }
        return false;
    }

    public enum MemSize {

        BYTE(1), WORD(2), DWORD(4);
        private int dataSize;

        private MemSize(int dataSize) {
            this.dataSize = dataSize;
        }
    }
}
