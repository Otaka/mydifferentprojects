package com.nes.assembler;

/**
 * @author Dmitry
 */
public class ParseException extends RuntimeException {
    private final int line;

    public ParseException(String message, int line) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String getMessage() {
        return super.getMessage()+" Line number "+line;
    }
    
    

}
