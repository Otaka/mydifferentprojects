package com.simplepl.entity;

/**
 * @author sad
 */
public class Argument {

    private TypeReference type;
    private String name;

    public Argument(TypeReference type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public TypeReference getType() {
        return type;
    }

}
