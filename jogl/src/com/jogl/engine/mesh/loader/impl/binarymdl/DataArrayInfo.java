package com.jogl.engine.mesh.loader.impl.binarymdl;

/**
 * @author Dmitry
 */
public class DataArrayInfo {
    private final int size;
    private final String hashName;
    private final String name;
    private final String template;

    private final int num;
    private final int loc;

    public DataArrayInfo(int size, String hashName, String name, String template) {
        this.size = size;
        this.hashName = hashName;
        this.name = name;
        this.template = template;
        this.num = -1;
        this.loc = -1;
    }

    public DataArrayInfo(int size, String hashName, String name, String template, int num, int loc) {
        this.size = size;
        this.hashName = hashName;
        this.name = name;
        this.template = template;
        this.num = num;
        this.loc = loc;
    }

    public int getNum() {
        return num;
    }

    public int getLoc() {
        return loc;
    }

    public int getSize() {
        return size;
    }

    public String getHashName() {
        return hashName;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

}
