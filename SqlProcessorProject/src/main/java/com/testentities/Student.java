package com.testentities;

/**
 * @author sad
 */
public class Student {

    private int id;
    private String name;
    private int houseId;

    public Student(int id, String name, int houseId) {
        this.id = id;
        this.name = name;
        this.houseId = houseId;
    }

    public int getId() {
        return id;
    }

    public int getHouseId() {
        return houseId;
    }

    public String getName() {
        return name;
    }

}
