package com.simplecas4j.ast;

/**
 * @author Dmitry
 */
public class Ast {

    private String type;
    private String value;
    private Ast[] children;

    public String getType() {
        return type;
    }

    public Ast setType(String type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Ast setValue(String value) {
        this.value = value;
        return this;
    }

    public Ast[] getChildren() {
        return children;
    }

    public Ast setChildren(Ast[] children) {
        this.children = children;
        return this;
    }
}
