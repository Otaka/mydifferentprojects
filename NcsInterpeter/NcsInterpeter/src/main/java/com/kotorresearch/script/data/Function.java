package com.kotorresearch.script.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class Function {

    private String functionName;
    private int functionIndex;
    private String returnType;
    private List<String> arguments = new ArrayList<>();
    private boolean usedInKotor;

    public Function(String functionName, int functionIndex, String returnType) {
        this.functionName = functionName;
        this.functionIndex = functionIndex;
        this.returnType = returnType;
    }

    public void setUsedInKotor(boolean usedInKotor) {
        this.usedInKotor = usedInKotor;
    }

    public boolean isUsedInKotor() {
        return usedInKotor;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public int getFunctionIndex() {
        return functionIndex;
    }

    public String getFunctionName() {
        return functionName;
    }

}
