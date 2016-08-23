package com.gui;

/**
 * @author Dmitry
 */
public class StringRow {
    private String text;
    private int width;
    private int height;

    public StringRow(String text, int length, int height) {
        this.text = text;
        this.width = length;
        this.height = height;
    }

    public StringRow() {
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
