package com.simplepl.grammar.ast;

import java.util.ArrayList;
import java.util.List;

public class Ast {

    private String name;
    public List<Ast> attributes = new ArrayList<>();
    public List<Ast> children = new ArrayList<>();

    public Ast(String name) {
        this.name = name;
    }

    public List<Ast> getAttributes() {
        return attributes;
    }

    public List<Ast> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}
