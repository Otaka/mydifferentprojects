package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FunctionInfo {

    private String name;
    private Expressions expressions = new Expressions();
    private TypeReference returnType;
    private List<Argument> arguments = new ArrayList<>();

    public void setReturnType(TypeReference returnType) {
        this.returnType = returnType;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public TypeReference getReturnType() {
        return returnType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Expressions getExpressions() {
        return expressions;
    }

    public String getName() {
        return name;
    }
}
