package com.simplecas4j.ast;

/**
 * @author sad
 */
public class AstHolder {

    private Ast ast;

    public AstHolder(Ast ast) {
        this.ast = ast;
    }

    public void setAst(Ast ast) {
        this.ast = ast;
    }

    public Ast getAst() {
        return ast;
    }

}
