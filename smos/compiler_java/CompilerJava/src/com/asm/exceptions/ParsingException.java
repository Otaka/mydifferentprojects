package com.asm.exceptions;

/**
 * @author sad
 */
public class ParsingException extends RuntimeException{
private int line;
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message,int line) {
        super(message);
        this.line = line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
