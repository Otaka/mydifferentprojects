package com.macro.tokenizer;

/**
 * @author sad
 */
public class Tokenizer {

    public static final char NO_CHAR = '\uffff';
    private char[] array;
    private int currentPosition = 0;
    private int currentLineNumber = 0;

    public Tokenizer(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text agrument should not be null");
        }

        this.array = text.toCharArray();
    }

    protected int getIndex() {
        return currentPosition;
    }

    public int getLineNumber() {
        return currentLineNumber;
    }

    protected char next() {
        char value = get(currentPosition);
        currentPosition++;
        if (value == '\n') {
            currentLineNumber++;
        }
        return value;
    }

    protected char peek(int charCount) {
        return get(currentPosition + charCount);
    }

    protected char peek() {
        return peek(0);
    }

    protected boolean eof() {
        return currentPosition < 0 || currentPosition >= array.length;
    }

    protected char get(int index) {
        if (index < 0 || index >= array.length) {
            return NO_CHAR;
        }
        return array[index];
    }

    private Token processSpaceToken() {
        StringBuilder sb = new StringBuilder();
        int lineNumber = getLineNumber();
        int index = getIndex();
        while (true) {
            char c = peek();
            if (isSpaceLikeChar(c)) {
                sb.append(c);
                next();
            } else {
                break;
            }
        }
        return new Token(sb.toString(), index, lineNumber, TokenType.Space);
    }

    private Token processData() {
        char c = peek();
        if (c == NO_CHAR) {
            return null;
        }

        int line = getLineNumber();
        int index = getIndex();
        if (Character.isJavaIdentifierStart(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            next();
            while (true) {
                c = peek();
                if (Character.isJavaIdentifierPart(c)) {
                    sb.append(c);
                    next();
                } else {
                    break;
                }
            }
            return new Token(sb.toString(), index, line, TokenType.Data);
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            next();
            while (true) {
                c = peek();
                if (Character.isDigit(c) || c == '.') {
                    sb.append(c);
                    next();
                } else {
                    break;
                }
            }
            return new Token(sb.toString(), index, line, TokenType.Data);
        }

        next();
        return new Token(String.valueOf(c), index, line, TokenType.Data);
    }

    /**
     Handling 'string' or "string" tokens
     */
    private Token processStringToken() {
        StringBuilder sb = new StringBuilder();
        char c = peek();
        sb.append(c);
        char quoteSymbol = c;
        next();
        int line = getLineNumber();
        int index = getIndex();

        while (true) {
            c = peek();
            if (c == quoteSymbol) {
                sb.append(c);
                next();
                break;
            }
            if (c == '\\') {//we should process cases where we have "string \"escaped string\" string"
                if (peek(1) == quoteSymbol) {
                    next();
                    next();
                    sb.append('\\').append(quoteSymbol);
                    continue;
                }
            }
            if (c == NO_CHAR) {
                throw new IllegalArgumentException("Reached eof while parsing string token. It seems you did not close \"string\". Token [" + quoteSymbol + getExampleTextFromStringBuilder(sb, 10) + "]");
            }
            if (c == '\n') {
                throw new IllegalArgumentException("Reached end of line while parsing string token. It seems you did not close \"string\". Token [" + quoteSymbol + getExampleTextFromStringBuilder(sb, 10) + "]");
            }
            next();
            sb.append(c);
        }
        return new Token(sb.toString(), index, line, TokenType.Data);
    }

    private String getExampleTextFromStringBuilder(StringBuilder sb, int length) {
        if (sb.length() > length) {
            return sb.substring(0, length) + "...";
        } else {
            return sb.toString();
        }
    }

    /**
     Handling //it is comment until the new line symbol
     */
    private Token processSingleLineComment() {
        int index = getIndex();
        int line = getLineNumber();
        next();
        next();//skip "//" part
        StringBuilder sb = new StringBuilder();
        sb.append("//");
        while (true) {
            char c = peek();
            if (c == '\n' || c == NO_CHAR) {
                break;
            }
            sb.append(c);
            next();
        }

        return new Token(sb.toString(), index, line, TokenType.Comment);
    }

    /**
     Handling /*multiline comment<i>*</i><i>/</i>
     */
    private Token processMultiLineComment() {
        int index = getIndex();
        int line = getLineNumber();
        next();
        next();//skip "/*" part
        StringBuilder sb = new StringBuilder();
        sb.append("/*");
        while (true) {
            char c = peek();
            if (c == '*' && peek(1) == '/') {
                next();
                next();//skip "*/"
                sb.append("*/");
                break;
            }

            sb.append(c);
            next();
        }

        return new Token(sb.toString(), index, line, TokenType.Comment);
    }

    private boolean isSpaceLikeChar(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f';
    }

    public Token nextTokenSkipSpaceAndComments() {
        while (true) {
            Token t = nextToken();
            if (t == null) {
                return null;
            }
            if (t.getTokenType() == TokenType.Data) {
                return t;
            }
        }
    }
    
    public Token nextTokenSkipComment() {
        while (true) {
            Token t = nextToken();
            if (t == null) {
                return null;
            }
            if (t.getTokenType() == TokenType.Data || t.getTokenType() == TokenType.Space) {
                return t;
            }
        }
    }

    public Token nextToken() {
        char c = peek();
        if (c == NO_CHAR) {
            return null;
        }

        if (isSpaceLikeChar(c)) {
            return processSpaceToken();
        }

        if (c == '/') {
            char nextSymbol = peek(1);
            if (nextSymbol == '/') {
                return processSingleLineComment();
            } else if (nextSymbol == '*') {
                return processMultiLineComment();
            }
        }

        if (c == '\'' || c == '"') {
            return processStringToken();
        }

        return processData();
    }
}
