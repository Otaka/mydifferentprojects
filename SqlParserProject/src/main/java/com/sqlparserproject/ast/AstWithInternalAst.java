package com.sqlparserproject.ast;

/**
 * @author sad
 */
public abstract class AstWithInternalAst extends Ast{
    private Ast internal;

    public AstWithInternalAst(Ast internal) {
        this.internal = internal;
    }

    public Ast getInternal() {
        return internal;
    }

    
    
}
