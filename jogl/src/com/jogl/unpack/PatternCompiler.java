package com.jogl.unpack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
class PatternCompiler {
    private char[] patternBuffer;
    private int index;

    public PatternCompiler() {

    }

    public PackPattern compilePattern(String pattern) {
        patternBuffer = pattern.toCharArray();
        index = 0;
        List<BasePacker> packer = compileExpression();
        return new PackPattern(packer);
    }

    private List<BasePacker> compileExpression() {
        List<BasePacker> packers = new ArrayList<>();
        outer:
        while (true) {
            char c = get();
            Quantificator quantificator = getSize();
            switch (c) {
                case (char) -1:
                    break outer;
                case '\t':
                case ' ':
                    continue;
                case 'x':
                case 'X'://skip byte
                    packers.add(new SkipPacker(quantificator));
                    continue;
                case 'Z'://ascii zero terminated string
                    if (quantificator == null) {
                        packers.add(new ZCharPacker());
                    } else {
                        if (quantificator instanceof GreedyQuantificator) {
                            packers.add(new ZeroTerminatedStringPacker(-1));
                        } else if (quantificator instanceof FixedQuantificator) {
                            packers.add(new ZeroTerminatedStringPacker(((FixedQuantificator) quantificator).getMax()));
                        }
                    }
                    break;
                case 'C'://unsigned byte
                    packers.add(new BytePacker(quantificator, true));
                    break;
                case 'c'://signed byte
                    packers.add(new BytePacker(quantificator, false));
                    break;
                case 'S'://unsigned short
                    packers.add(new ShortPacker(quantificator, true));
                    break;
                case 's'://signed short
                    packers.add(new ShortPacker(quantificator, false));
                    break;
                case 'Q'://unsigned long
                    packers.add(new LongPacker(quantificator, true));
                    break;
                case 'q'://signed long
                    packers.add(new LongPacker(quantificator, false));
                    break;

                case 'I'://unsigned int
                    packers.add(new IntPacker(quantificator, true));
                    break;
                case 'L'://unsigned int
                    packers.add(new UnsignedIntReturningLongPacker(quantificator));
                    break;
                case 'i'://signed int
                case 'l'://signed int
                    packers.add(new IntPacker(quantificator, false));
                    break;
                case 'f'://float
                    packers.add(new FloatPacker(quantificator));
                    break;
                case 'd'://double
                    packers.add(new DoublePacker(quantificator));
                    break;

            }
        }
        return packers;
    }

    Quantificator getSize() {
        char next = peek(0);
        if (next == '*') {
            skip();
            return new GreedyQuantificator();
        } else if (next >= '0' && next <= '9') {
            int number = getInteger();
            return new FixedQuantificator(number);
        } else if (next == '[') {
            Quantificator quantificator = null;
            skip();
            skipSpaces();
            if (isNextInteger()) {
                int size = getInteger();
                quantificator = new FixedQuantificator(size);
            }

            skipSpaces();
            char endBracket = get();
            if (endBracket != ']') {
                throw new IllegalArgumentException("Open bracket should ends with close bracket in pattern " + String.valueOf(patternBuffer));
            }

            return quantificator;
        }

        return null;
    }

    void skipSpaces() {
        char c = peek(0);
        if (c == ' ' || c == '\t') {
            while (true) {
                c = get();
                if (!(c == ' ' || c == '\t')) {
                    rewind();
                    break;
                }
            }
        }
    }

    boolean isNextInteger() {
        char c = peek(0);
        return c >= '0' && c <= '9';
    }

    int getInteger() {
        int digit = 0;
        while (true) {
            char c = get();
            if (!(c >= '0' && c <= '9')) {
                rewind();
                break;
            }
            int num = c - '0';
            digit *= 10;
            digit += num;
        }

        return digit;
    }

    void rewind() {
        index--;
    }

    void skip(int count) {
        index++;
    }

    void skip() {
        skip(1);
    }

    char get() {
        if (index >= patternBuffer.length) {
            return (char) -1;
        }

        char value = patternBuffer[index];
        index++;
        return value;
    }

    char peek(int count) {
        int ni = index + count;
        if (ni >= patternBuffer.length) {
            return (char) -1;
        }
        return patternBuffer[ni];
    }
}
