package com.simplepl.entity;

import com.simplepl.entity.types.Type;

/**
 * @author sad
 */
public class TypeReference {

    private String typeName;
    private Type type;
    private boolean pointer;
    private ModuleInfo ownerModuleInfo;

    public TypeReference(String typeName) {
        this.typeName = typeName;
        pointer = false;
    }

    public void setOwnerModuleInfo(ModuleInfo ownerModuleInfo) {
        this.ownerModuleInfo = ownerModuleInfo;
    }

    public ModuleInfo getOwnerModuleInfo() {
        return ownerModuleInfo;
    }

    public void setType(Type type) {
        this.type = type;
    }

    
    
    public Type getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isPointer() {
        return pointer;
    }

    public void setPointer(boolean isPointer) {
        this.pointer = isPointer;
    }

    @Override
    public String toString() {
        if (pointer) {
            return "pointer[" + typeName + "]";
        } else {
            return typeName;
        }
    }
}
