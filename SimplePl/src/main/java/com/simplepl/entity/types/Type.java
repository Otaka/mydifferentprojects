package com.simplepl.entity.types;

/**
 * @author sad
 */
public class Type {

    private String typeName;
    private Object internal;

    public void setInternal(Object internal) {
        this.internal = internal;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Object getInternal() {
        return internal;
    }

    public String getTypeName() {
        return typeName;
    }

}
