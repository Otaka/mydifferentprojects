package com.simplecas4j.ast;

/**
 * @author sad
 */
public class AstHolderFactory {

    private Ast example;

    public AstHolderFactory(Ast example) {
        this.example = example;
    }

    public Ast construct() {
        return example.deepClone();
    }
}
