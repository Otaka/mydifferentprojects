package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FileInfo {

    private String packagePath;
    private List<Import> imports = new ArrayList<>();
    private List<Function> functionList = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();
    private List<Variable> globalVariables = new ArrayList<>();
    private List<Type> types = new ArrayList<>();

    public String getPackagePath() {
        return packagePath;
    }

    public FileInfo(String packagePath) {
        this.packagePath = packagePath;
    }

    public List<Function> getFunctionList() {
        return functionList;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public List<Variable> getGlobalVariables() {
        return globalVariables;
    }

    public List<Type> getTypes() {
        return types;
    }

    public List<Import> getImports() {
        return imports;
    }

}
