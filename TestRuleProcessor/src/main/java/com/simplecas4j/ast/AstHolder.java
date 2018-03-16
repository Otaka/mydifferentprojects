package com.simplecas4j.ast;

import com.simplecas4j.rule.RuleType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class AstHolder {

    private Ast ast;
    private AstHolder parent;

    public AstHolder(Ast ast) {
        this.ast = ast;
    }

    public void setDirty(boolean dirty) {
        ast.setDirty(dirty);
    }

    public void setDirtyRecursivelyToParents(boolean value) {
        setDirty(value);
        if (parent != null) {
            parent.setDirtyRecursivelyToParents(value);
        }
    }

    public boolean isDirty() {
        return ast.isDirty();
    }

    public void setParent(AstHolder parent) {
        this.parent = parent;
    }

    public AstHolder getParent() {
        return parent;
    }

    public void setAst(Ast ast) {
        this.ast = ast;
    }

    public Ast getAst() {
        return ast;
    }

    @Override
    public String toString() {
        return "" + ast.getType() + " " + ast.getValue();
    }

    public List<AstHolder> getChildren() {
        return ast.getChildren();
    }

    public AstHolder setChildren(List<AstHolder> children) {
        ast.setChildren(children);
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setParent(this);
            }
        }
        return this;
    }

    public AstHolder addChild(AstHolder child) {
        getChildren().add(child);
        child.setParent(this);
        return this;
    }

    public AstHolder removeChild(AstHolder child) {
        if (!getChildren().remove(child)) {
            throw new IllegalArgumentException("You try to remove ast [" + child + "] from [" + this + "] but it is not child of this parent");
        }
        return this;
    }

    public AstHolder removeChild(int index) {
        getChildren().remove(index);
        return this;
    }

    public AstHolder removeThisFromParent() {
        if (getParent() == null) {
            throw new IllegalStateException("You try to remove ast [" + this + "] from parent, but there is no parent in this ast");
        }

        getParent().removeChild(this);
        return this;
    }

    public RuleType getType() {
        return ast.getType();
    }

    public String getValue() {
        return ast.getValue();
    }

    public AstHolder setValue(String value) {
        ast.setValue(value);
        return this;
    }

    public String deepToString() {
        switch (getType()) {
            case NUMBER:
            case VAR:
                String val = getValue();
                List<AstHolder> children2 = getChildren();
                if (children2 != null && !children2.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ERRORCHILD[");
                    for (int i = 0; i < children2.size(); i++) {
                        if (i != 0) {
                            sb.append(",");
                        }
                        sb.append(children2.get(i).deepToString());
                    }
                    sb.append("]");
                } else {
                    return val;
                }

                return val;
            case PARENTHESES:
                if (!getChildren().isEmpty()) {
                    return "(" + getChildren().get(0).deepToString() + ")";
                }
                return "()";
            case OPERATOR:
                if (isInfixOperator(getValue())) {
                    List<AstHolder> children = getChildren();
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < children.size(); i++) {
                        if (i != 0) {
                            sb.append(getValue());
                        }
                        sb.append(children.get(i).deepToString());
                    }
                    sb.append("]");
                    return sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[").append(getValue()).append(": ");
                    List<AstHolder> children = getChildren();
                    int childrenCount = children.size();
                    for (int i = 0; i < childrenCount; i++) {
                        if (i != 0) {
                            sb.append(",");
                        }

                        sb.append(children.get(i).deepToString());
                    }
                    sb.append("]");
                    return sb.toString();
                }
            default:
                throw new IllegalArgumentException("[" + getType() + "] is not implemented in deepToString method");
        }
    }

    private boolean isInfixOperator(String op) {
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            return true;
        }
        return false;
    }

    public void swapContentWithAnotherAstHolder(AstHolder other, boolean swapChildren) {
        Ast temp = ast;
        ast = other.ast;
        other.ast = temp;
        if (swapChildren) {
            List<AstHolder> tempChildren = ast.getChildren();
            ast.setChildren(other.getChildren());
            other.setChildren(tempChildren);
        } else {
            for (AstHolder child : getChildren()) {
                child.setParent(this);
            }
            for (AstHolder child : other.getChildren()) {
                child.setParent(other);
            }

        }
    }

    public AstHolder deepClone() {
        AstHolder newHolder = new AstHolder(ast.clone());
        newHolder.parent = parent;

        if (ast.getChildren() != null) {
            List<AstHolder> originalChildren = ast.getChildren();
            List<AstHolder> newChildren = new ArrayList<AstHolder>();
            for (int i = 0; i < originalChildren.size(); i++) {
                newChildren.add(originalChildren.get(i).deepClone());
            }

            newHolder.setChildren(newChildren);
        }

        return newHolder;
    }
}
