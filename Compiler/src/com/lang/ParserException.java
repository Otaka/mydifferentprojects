package com.lang;

/**
 *
 * @author Dmitry
 */
public class ParserException extends RuntimeException {

    private int line = -1;
    private int offset = -1;

    public ParserException(String message) {
        super(message);
    }
    public ParserException(String message, int line, int offset) {
        super(message);
        this.line=line;
        this.offset=offset;
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        if (line == -1 && offset == -1) {
            return super.getMessage();
        }
        if (line != -1 && offset != -1) {
            return super.getMessage() + " Line:" + line + " Offset:" + offset;
        }
        if (line == -1) {
            return super.getMessage() + " Offset:" + offset;
        }

        return super.getMessage() + " Line:" + line;
    }
}
