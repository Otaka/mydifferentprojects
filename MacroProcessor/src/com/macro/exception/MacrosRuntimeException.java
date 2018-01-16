package com.macro.exception;

/**
 * @author sad
 */
public class MacrosRuntimeException extends RuntimeException{

    public MacrosRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MacrosRuntimeException(String message) {
        super(message);
    }
}
