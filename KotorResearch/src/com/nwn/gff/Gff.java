package com.nwn.gff;

/**
 * @author sad
 */
public class Gff {

    private final String type;
    private final String version;
    private final GffStructure root;

    public Gff(String type, String version, GffStructure root) {
        this.type = type;
        this.version = version;
        this.root = root;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

}
