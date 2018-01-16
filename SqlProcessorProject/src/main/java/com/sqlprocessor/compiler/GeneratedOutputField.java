package com.sqlprocessor.compiler;

/**
 * @author sad
 */
public class GeneratedOutputField {

    private Class type;
    private String name;
    private String sourceCode;

    public GeneratedOutputField(Class type, String name, String sourceCode) {
        this.type = type;
        this.name = name;
        this.sourceCode = sourceCode;
    }

    public String getName() {
        return name;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public Class getType() {
        return type;
    }

}
