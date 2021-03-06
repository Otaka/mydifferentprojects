package com.simplepl.entity;

import com.simplepl.astinfoextractor.ModuleTypeFinder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class ModuleInfo {

    private String module;
    private List<Import> imports = new ArrayList<>();
    private List<FunctionInfo> functionList = new ArrayList<>();
    private List<StructureInfo> structuresList = new ArrayList<>();
    private List<DefTypeInfo> deftypesList = new ArrayList<>();
    private List<VariableInfo> globalVariablesList = new ArrayList<>();
    private boolean typesProcessed = false;

    public List<VariableInfo> getGlobalVariablesList() {
        return globalVariablesList;
    }

    public List<DefTypeInfo> getDeftypesList() {
        return deftypesList;
    }

    public List<StructureInfo> getStructuresList() {
        return structuresList;
    }

    public void setTypesProcessed(boolean typesProcessed) {
        this.typesProcessed = typesProcessed;
    }

    public boolean isTypesProcessed() {
        return typesProcessed;
    }

    public String getModule() {
        return module;
    }

    public ModuleInfo(String module) {
        this.module = module;
    }

    public List<FunctionInfo> getFunctionList() {
        return functionList;
    }

    public List<Import> getImports() {
        return imports;
    }

    @Override
    public String toString() {
        return getModule();
    }

    public ModuleTypeFinder createModuleTypeFinder(Context context) {
        ModuleTypeFinder moduleTypeFinder = new ModuleTypeFinder(context);
        moduleTypeFinder.addImport(getModule());
        for (Import importObject : getImports()) {
            moduleTypeFinder.addImport(importObject.getPath());
        }

        return moduleTypeFinder;
    }
}
