package com.macro.tokenizer;

/**
 * @author sad
 */
public class Token {

    private final String value;
    private final int line;
    private final int index;
    private final TokenType tokenType;

    public Token(String value, int index, int line, TokenType tokenType) {
        this.index = index;
        this.value = value;
        this.line = line;
        this.tokenType = tokenType;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "["+tokenType+"] "+value;
    }
    
    
}
