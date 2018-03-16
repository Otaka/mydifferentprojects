package com.simplecas4j.ast;

import com.simplecas4j.rule.RuleType;
import java.util.List;

/**
 * @author Dmitry
 */
public class Ast {

    private RuleType type;
    private String value;
    private List<AstHolder> children;
    private boolean dirty = true;

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    
    
    public RuleType getType() {
        return type;
    }

    public Ast setType(RuleType type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Ast setValue(String value) {
        this.value = value;
        return this;
    }

    protected List<AstHolder> getChildren() {
        return children;
    }

    protected Ast setChildren(List< AstHolder> children) {
        this.children = children;
        return this;
    }

    @Override
    public String toString() {
        return "" + getType() + " " + getValue();
    }

    public Ast clone() {
        Ast ast = new Ast();
        ast.children = children;
        ast.type = type;
        ast.value = value;
        return ast;
    }
}
