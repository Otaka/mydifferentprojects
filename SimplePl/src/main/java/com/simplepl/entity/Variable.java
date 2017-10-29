package com.simplepl.entity;

/**
 * @author sad
 */
public class Variable {

    private Type type;
    private String name;

    public Variable(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return ""+type+":"+name;
    }

}
