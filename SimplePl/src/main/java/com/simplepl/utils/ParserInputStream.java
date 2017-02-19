package com.simplepl.utils;

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

    public ParserInputStream(String string) {
        if (string == null) {
            throw new IllegalArgumentException("grammar agrument cannot be null");
        }

        this.array = string.toCharArray();
    }

    public int size() {
        return array.length;
    }

    public int getIndex() {
        return index;
    }

    public String line(int start, int end) {
        return new String(array, start, end - start);
    }

    public char next() {
        char value = get(index);
        index++;
        checkNewLine();
        return value;
    }

    private void checkNewLine() {
        if (lastIndex >= index) {
            return;
        }

        lastIndex = index;
        char symbol = peek(0);
        /*if(symbol=='\r' && peek(1)=='\n'){
            newLineSymbolCount=2;
            symbol=next();
        }*/
        if (symbol == '\n') {
            int start = lastNewLineIndex;
            int end = index - (newLineSymbolCount - 1);
            Line line = new Line(start, end, currentLineCounter);
            lines.add(line);
            lastNewLineIndex = index + 1;
            currentLineCounter++;
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

    public char peek(int charCount) {
        return get(index + charCount);
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

    public void skip(int count) {
        for (int i = 0; i < count; i++) {
            next();
        }
    }

    public void skipBlank() {
        while (true) {
            char c = peek(0);
            if (Character.isSpaceChar(c) || c == '\t' || c == '\n' || c == '\r') {
                next();
            } else {
                break;
            }
        }
    }

    public String readWord() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = peek(0);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(next());
            } else {
                break;
            }
        }
        return sb.length() == 0 ? null : sb.toString();
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
