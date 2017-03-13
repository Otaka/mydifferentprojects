package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class Structure {

    private String name;
    private List<Variable> fields = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Variable> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return name;
    }

    
}
