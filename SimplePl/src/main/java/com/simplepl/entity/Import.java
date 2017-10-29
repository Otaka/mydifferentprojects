package com.simplepl.entity;

/**
 * @author sad
 */
public class Import {
    private String path;
    private boolean isStatic;

    public Import(String path, boolean isStatic) {
        this.path = path;
        this.isStatic = isStatic;
    }

    public String getPath() {
        return path;
    }

    public boolean isStatic() {
        return isStatic;
    }
    
}
