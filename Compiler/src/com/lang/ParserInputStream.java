package com.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class ParserInputStream {

    public static final char NO_CHAR = '\uffff';
    private char[] array;
    private int index = 0;
    private int lastIndex = 0;
    private int lastNewLineIndex = 0;
    private int currentLineCounter = 0;
    private final List<Line> lines = new ArrayList<>();
    private int newLineSymbolCount = 1;
    private int tabLevel = 0;
    boolean isStartSpaces = false;//at start of line should be only tabs, not spaces

    public ParserInputStream(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text agrument cannot be null");
        }

        this.array = text.toCharArray();
    }

    public int getCurrentLine() {
        return currentLineCounter;
    }

    public int getTabLevel() {
        return tabLevel;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String line(int start, int end) {
        return new String(array, start, end - start);
    }

    public char next() {
        char value = get(index);

        checkNewLine();
        index++;
        return value;
    }

    private void checkNewLine() {
        if (lastIndex >= index) {
            return;
        }

        lastIndex = index;
        char symbol = peek(0);
        if (symbol == '\r' && peek(1) == '\n') {
            newLineSymbolCount = 2;
            tabLevel = 0;
            isStartSpaces = true;
            symbol = next();
        }
        if (symbol == '\n') {
            int start = lastNewLineIndex;
            int end = index - (newLineSymbolCount - 1);
            Line line = new Line(start, end, currentLineCounter);
            lines.add(line);
            lastNewLineIndex = index + 1;
            currentLineCounter++;
            isStartSpaces = true;
            tabLevel = 0;
        } else if (isStartSpaces) {
            if (!(Character.isSpaceChar(symbol) || symbol == '\t' || symbol == '\n' || symbol == '\r')) {
                isStartSpaces = false;
            }
        }
    }

    public List<Line> getLines() {
        return lines;
    }

    public void rewind() {
        index--;
        if (index < 0) {
            index = 0;
        }
    }

    public int getOffset() {
        return lastNewLineIndex - index;
    }

    public char peek(int charCount) {
        return get(index + charCount);
    }

    public char peek() {
        return get(index + 0);
    }

    public boolean eof() {
        return index < 0 || index >= array.length;
    }

    public char get(int index) {
        if (index < 0 || index >= array.length) {
            return NO_CHAR;
        }
        return array[index];
    }

    public boolean skipBlankEnsureNewString() {
        int currentLine = currentLineCounter;
        skipBlank();
        return currentLine != currentLineCounter;
    }

    public boolean skipBlankEnsureCurrentString() {
        int currentLine = currentLineCounter;
        skipBlank();
        return currentLine != currentLineCounter;
    }

    public void skipBlank() {
        OUTER:
        while (true) {
            char c = peek(0);
            switch (c) {
                case '\n':
                case '\r':
                    tabLevel = 0;
                    isStartSpaces = true;
                    next();
                    break;
                case '\t':
                case ' ':
                    if (isStartSpaces) {
                        tabLevel++;
                    }
                    next();
                    break;
                default:
                    isStartSpaces = false;
                    break OUTER;
            }
        }
    }

    public String readWord() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = peek(0);
            if (Character.isJavaIdentifierPart(c) || c == '"') {
                sb.append(next());
            } else {
                break;
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private char processEscapeSymbol(char c) {
        switch (c) {
            case 'b':
                return '\b';
            case 't':
                return '\t';
            case 'r':
                return '\r';
            case 'n':
                return '\n';
            case 'f':
                return '\f';
            case '"':
                return '"';
            case '\\':
                return '\\';
            default:
                throw new RuntimeException("Wrong escape sequence [\\" + c + "]");
        }
    }

    /**
     * read quoted string(i.e. "my string \n new line")
     */
    public String readQuotedString() {
        StringBuilder sb = new StringBuilder();
        char c = next();
        boolean escape = false;
        if (c == '\"') {
            while (true) {
                c = next();
                if (c == NO_CHAR) {
                    throw new RuntimeException("String [" + sb.toString() + "] should end with quote");
                } else if (c == '\\') {
                    escape = true;
                } else if (escape) {
                    c = next();
                    c = processEscapeSymbol(c);
                    sb.append(c);
                    escape = false;
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                }
            }
        } else {
            rewind();
            while (true) {
                c = next();
                if (c == NO_CHAR) {
                    break;
                } else if (c == '\\') {
                    escape = true;
                } else if (escape) {
                    c = next();
                    c = processEscapeSymbol(c);
                    sb.append(c);
                    escape = false;
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    public boolean skipUntil(char... untilSymbol) {
        while (true) {
            char c = next();
            for (char us : untilSymbol) {
                if (c == us) {
                    return true;
                }
            }

            if (eof()) {
                return false;
            }
        }
    }

    /**
     * Class that allow to determine boundaries of each line in given text
     */
    public static class Line {

        private final int start;
        private final int end;
        private final int lineNumber;

        public Line(int start, int end, int lineNumber) {
            this.start = start;
            this.end = end;
            this.lineNumber = lineNumber;
        }

        public int getEnd() {
            return end;
        }

        public int getStart() {
            return start;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
}
