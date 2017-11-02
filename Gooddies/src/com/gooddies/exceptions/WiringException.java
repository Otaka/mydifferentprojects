package com.gooddies.exceptions;

public class WiringException extends RuntimeException {

    public WiringException(String message) {
        super(message);
    }

    public WiringException(Throwable cause) {
        super(cause);
    }

    public WiringException(String message, Throwable cause) {
        super(message, cause);
    }
}
