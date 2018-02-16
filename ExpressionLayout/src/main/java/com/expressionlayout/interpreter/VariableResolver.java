package com.expressionlayout.interpreter;

/**
 * @author sad
 */
public abstract class VariableResolver {

    public NumberObject getVariable(String variableName) {
        return null;
    }

    public NumberObject getVariableField(String variableName, String field) {
        return null;
    }

    public boolean setVariable(String variableName, NumberObject no) {
        return false;
    }

    public boolean setVariableField(String variableName, String field, NumberObject no) {
        return false;
    }

    public NumberObject suffixResolver(NumberObject numberObject) {
        return null;
    }

}
