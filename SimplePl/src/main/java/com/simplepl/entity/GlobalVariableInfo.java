package com.simplepl.entity;

/**
 * @author sad
 */
public class GlobalVariableInfo {

    private String name;
    private TypeReference type;

    public GlobalVariableInfo(String name, TypeReference type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeReference getType() {
        return type;
    }
}
