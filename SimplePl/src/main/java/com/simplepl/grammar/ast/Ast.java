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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public List<Ast> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ast:"+name;
    }

}
