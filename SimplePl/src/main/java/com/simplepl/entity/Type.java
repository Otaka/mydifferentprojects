package com.simplepl.entity;

/**
 * @author sad
 */
public class Type {

    private String name;
    private String packagePath;
    private boolean pointer;
    private Type parent;

    public void setParent(Type parent) {
        this.parent = parent;
    }

    public Type getParent() {
        return parent;
    }

    public boolean isPointer() {
        return pointer;
    }

    public void setPointer(boolean pointer) {
        this.pointer = pointer;
    }

    public String getFullPath() {
        if (packagePath == null) {
            return name;
        }
        return packagePath + "." + name;
    }

    public String getName() {
        return name;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

}
