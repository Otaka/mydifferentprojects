package com.gui;

/**
 * @author Dmitry
 */
public class Connection {
    private final Node node;

    public Connection(Node node) {
        this.node = node;
    }

    private Connection() {
        this.node = null;
    }

    public Node getNode() {
        return node;
    }

}
