package com.simplepl.entity;

import com.simplepl.entity.types.Type;

/**
 * @author sad
 */
public class TypeReference {

    private String typeName;
    private Type type;
    private TypeReference pointer;
    private boolean isPointer;

    public TypeReference(String typeName) {
        this.typeName = typeName;
        isPointer = false;
    }

    public TypeReference(TypeReference pointer) {
        this.pointer = pointer;
        isPointer = true;
    }

    public Type getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isPointer() {
        return isPointer;
    }

    public TypeReference getPointer() {
        return pointer;
    }

}
