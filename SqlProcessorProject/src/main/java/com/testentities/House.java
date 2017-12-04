package com.testentities;

/**
 * @author sad
 */
public class House {

    private int id;
    private String name;

    public House(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
