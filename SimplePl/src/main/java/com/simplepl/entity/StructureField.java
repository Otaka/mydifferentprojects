package com.simplepl.entity;

/**
 * @author Dmitry
 */
public class StructureField {

    private String name;
    private TypeReference type;

    public StructureField(String name, TypeReference type) {
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
