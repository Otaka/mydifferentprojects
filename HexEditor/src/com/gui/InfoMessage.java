package com.gui;

/**
 * @author Dmitry
 */
public class InfoMessage {
    private String id;
    private String infoMessage;

    public InfoMessage(String id, String infoMessage) {
        this.id = id;
        this.infoMessage = infoMessage;
    }

    public String getId() {
        return id;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

}
