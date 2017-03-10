package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FileInfo {

    private List<Function> functionList = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();
    private List<Variable>globalVariables=new ArrayList<>();
    private List<Type>types=new ArrayList<>();
    public List<Function> getFunctionList() {
        return functionList;
    }

    public List<Structure> getStructures() {
        return structures;
    }

}
