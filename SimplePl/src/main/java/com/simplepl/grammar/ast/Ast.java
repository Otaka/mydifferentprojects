package com.simplepl.grammar.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ast {

    private String name;
    public Map<String, Object> attributes = new HashMap<>();
    public List<Ast> children = new ArrayList<>();

    public Ast(String name) {
        this.name = name;
    }

    public Ast getAttributeAst(String name) {
        return (Ast) attributes.get(name);
    }

    public boolean getAttributeBoolean(String name) {
        return (Boolean) attributes.get(name);
    }

    public boolean getAttributeBoolean(String name, boolean defaultValue) {
        Object value = attributes.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String)value);
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        throw new IllegalArgumentException("Cannot convert [" + value.getClass().getSimpleName() + "] to boolean for attribute [" + name + "]");
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public List<Ast> getChildren() {
        return children;
    }

    public void addChild(Ast ast) {
        children.add(ast);
    }

    public void addAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Ast:" + name;
    }
}
