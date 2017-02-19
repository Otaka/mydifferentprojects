package com.simplepl.grammar.comments;

/**
 * @author Dmitry
 */
public class Comment {

    private final String data;
    private final int position;
    private final boolean multiLine;

    public Comment(String data, int position, boolean isMultiLine) {
        this.data = data;
        this.position = position;
        this.multiLine = isMultiLine;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public String getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
