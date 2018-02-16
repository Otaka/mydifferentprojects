package com.expressionlayout.interpreter;

/**
 * @author sad
 */
public class Token {

    private String value;
    private TokenType type;
    private Object userObject;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public Object getUserObject() {
        return userObject;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + "[" + type + "]";
    }
}
