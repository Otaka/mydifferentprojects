package com.simplepl.entity;

/**
 * @author sad
 */
public class DefTypeInfo {

    private String name;
    private TypeReference typeReference;

    public DefTypeInfo(String name, TypeReference typeReference) {
        this.name = name;
        this.typeReference = typeReference;
    }

    public String getName() {
        return name;
    }

    public TypeReference getTypeReference() {
        return typeReference;
    }

}
