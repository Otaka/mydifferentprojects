package com.simplepl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class Function {
    private Type returnValue;
    private String name;
    private List<Annotation>annotations=new ArrayList<Annotation>();
    private List<Argument>arguments=new ArrayList<>();
    private Expressions expressions=new Expressions();

    public void setReturnValue(Type returnValue) {
        this.returnValue = returnValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public Expressions getExpressions() {
        return expressions;
    }

    public String getName() {
        return name;
    }

    public Type getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString() {
        return returnValue+" "+name;
    }
    
    
}
