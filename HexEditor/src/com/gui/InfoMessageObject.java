package com.gui;

/**
 * @author Dmitry
 */
public class InfoMessageObject {
    private final String id;
    private final Object message;

    public InfoMessageObject(String id, Object message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public Object getMessage() {
        return message;
    }

}
