package com.sqlprocessor.compiler.exception;

/**
 * @author sad
 */
public class CannotCompileClassException extends RuntimeException{

    public CannotCompileClassException(String message) {
        super(message);
    }

    public CannotCompileClassException(String message, Throwable cause) {
        super(message, cause);
    }

}
