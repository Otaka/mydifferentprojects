package com.kotorresearch.script.data;

import java.lang.reflect.Method;

/**
 * @author Dmitry
 */
public class ScriptFunction {

    private int index;
    private Object owner;
    private Method methodToCall;
    private Class[] arguments;

    public ScriptFunction(int index, Object owner, Method methodToCall, Class[] arguments) {
        this.index = index;
        this.owner = owner;
        this.methodToCall = methodToCall;
        this.arguments = arguments;
    }

    public Class[] getArguments() {
        return arguments;
    }

    public int getIndex() {
        return index;
    }

    public Method getMethodToCall() {
        return methodToCall;
    }

    public Object getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return methodToCall.getName();
    }

    
    
}
