package com.gooddies.exceptions;

/**
 * @author Dmitry
 */
public class ErrorMessageException extends RuntimeException {

    public ErrorMessageException(String message) {
        super(message);
    }

    public ErrorMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
