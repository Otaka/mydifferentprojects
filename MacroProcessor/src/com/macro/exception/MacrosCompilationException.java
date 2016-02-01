package com.macro.exception;

/**
 * @author sad
 */
public class MacrosCompilationException extends RuntimeException{

    public MacrosCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MacrosCompilationException(String message) {
        super(message);
    }
}
