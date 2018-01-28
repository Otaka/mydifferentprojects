package com.simplepl.intermediate;

import com.simplepl.entity.VariableInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry
 */
public class Scope {

    private Map<String, VariableInfo> variablesMap = new HashMap<>();

    public Map<String, VariableInfo> getVariablesMap() {
        return variablesMap;
    }

    public boolean contains(String name){
        return variablesMap.containsKey(name);
    }
    
    public void addVariable(VariableInfo varInfo){
        variablesMap.put(varInfo.getName(), varInfo);
    }
    
}
