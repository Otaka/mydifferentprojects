package com.simplepl.exception;

/**
 * @author Dmitry
 */
public class ParseException extends RuntimeException{
    private final int position;

    public ParseException(int position, String message) {
        super(message);
        this.position = position;
    }
    
}
