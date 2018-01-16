package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class StructureInfo {
    private String name;
    private List<StructureField>fields=new ArrayList<>();

    public StructureInfo(String name) {
        this.name = name;
    }

    public StructureInfo() {
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public List<StructureField> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }
}
