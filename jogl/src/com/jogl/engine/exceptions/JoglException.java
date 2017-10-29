package com.jogl.engine.exceptions;

/**
 * @author Dmitry
 */
public class JoglException extends RuntimeException {

    public JoglException(String message) {
        super(message);
    }

    public JoglException(String message, Throwable cause) {
        super(message, cause);
    }

}
