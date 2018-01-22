package com.simplepl.entity.types;

import com.simplepl.entity.ModuleInfo;
import com.simplepl.entity.TypeReference;

/**
 * @author sad
 */
public class Type {

    private String typeName;
    private Object internal;
    private ModuleInfo ownerModule;
    private TypeReference parent;

    public void setParent(TypeReference parent) {
        this.parent = parent;
    }

    public TypeReference getParent() {
        return parent;
    }

    public void setOwnerModule(ModuleInfo ownerModule) {
        this.ownerModule = ownerModule;
    }

    public ModuleInfo getOwnerModule() {
        return ownerModule;
    }

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

    @Override
    public String toString() {
        if (ownerModule != null) {
            return ownerModule.getModule() + ":" + getTypeName() + " " + getInternal().getClass().getSimpleName();
        }
        return getTypeName() + " " + getInternal().getClass().getSimpleName();
    }

}
