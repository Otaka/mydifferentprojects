package com.simplecas4j.ast;

import com.simplecas4j.rule.RuleType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class Ast {

    private RuleType type;
    private String value;

    private boolean dirty = true;

    private Ast parent;
    private String stringValue;
    private List<Ast> children;

    public Ast() {
    }

    public Ast setDirty(boolean dirty) {
        this.dirty = dirty;
        stringValue = null;
        return this;
    }

    public void setDirtyRecursivelyToParents(boolean value) {
        setDirty(value);
        if (parent != null) {
            parent.setDirtyRecursivelyToParents(value);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setParent(Ast parent) {
        this.parent = parent;
    }

    public Ast getParent() {
        return parent;
    }

    public Ast setType(RuleType type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "" + type + " " + value + " : " + deepToString();
    }

    public List<Ast> getChildren() {
        return children;
    }

    public Ast setChildren(List<Ast> children) {
        this.children = children;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setParent(this);
            }
        }
        return this;
    }

    public Ast addChild(Ast child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParent(this);
        return this;
    }

    public Ast removeChild(Ast child) {
        if (!getChildren().remove(child)) {
            throw new IllegalArgumentException("You try to remove ast [" + child + "] from [" + this + "] but it is not child of this parent");
        }
        return this;
    }

    public Ast removeChild(int index) {
        getChildren().remove(index);
        return this;
    }

    public Ast removeThisFromParent() {
        if (getParent() == null) {
            throw new IllegalStateException("You try to remove ast [" + this + "] from parent, but there is no parent in this ast");
        }

        Ast parent = getParent();
        parent.removeChild(this);
        parent.setDirtyRecursivelyToParents(true);
        return this;
    }

    public RuleType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Ast setValue(String value) {
        this.value = value;
        return this;
    }

    public Ast setValueUpdateDirtyStatus(String value) {
        this.value = value;
        setDirtyRecursivelyToParents(true);
        return this;
    }

    public String deepToString() {
        if (stringValue == null) {
            stringValue = internalDeepToString();
        }

        return internalDeepToString();
    }

    public String internalDeepToString() {

        switch (getType()) {
            case NUMBER:
            case VAR:
                String val = getValue();
                List<Ast> children2 = getChildren();
                if (children2 != null && !children2.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(val);
                    sb.append("ERRORCHILD[");
                    for (int i = 0; i < children2.size(); i++) {
                        if (i != 0) {
                            sb.append(",");
                        }
                        sb.append(children2.get(i).deepToString());
                    }
                    sb.append("]");
                    return sb.toString();
                } else {
                    return val;
                }
            case PARENTHESES:
                if (!getChildren().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("(").append(getChildren().get(0).deepToString()).append(")");
                    if (getChildren().size() > 1) {
                        sb.append("ERR_PAR_CHILD[");
                        for (int i = 1; i < getChildren().size(); i++) {
                            if (i != 1) {
                                sb.append(",");
                            }
                            sb.append(getChildren().get(i).deepToString());
                        }
                        sb.append("]");
                    }
                    return sb.toString();
                }
                return "()";
            case OPERATOR:
                if (isInfixOperator(getValue()) && (children != null && children.size() > 1)) {
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
                    int childrenCount = children == null ? 0 : children.size();
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

    public void setContentFromXAstAndRemoveX(Ast xAst) {
        if (xAst.getParent() != null) {
            xAst.removeThisFromParent();
        }
        type = xAst.type;
        value = xAst.value;
        children = xAst.children;
        if (children != null) {
            for (Ast child : children) {
                child.setParent(this);
            }
        }
        setDirtyRecursivelyToParents(true);
    }

    public void swapContent(Ast other) {
        RuleType tempRuleType = this.type;
        this.type = other.type;
        other.type = tempRuleType;

        String tempValue = this.value;
        this.value = other.value;
        other.value = tempValue;

        if (isChildOf(other)) {
            throw new IllegalArgumentException("When swap, asts cannot be in relation CHILD-PARENT");

        } else if (other.isChildOf(this)) {
            throw new IllegalArgumentException("When swap, asts cannot be in relation CHILD-PARENT");
        } else {
            List<Ast> tempChildren = children;
            children = other.getChildren();
            other.setChildren(tempChildren);
            if (children != null) {
                for (Ast child : children) {
                    child.setParent(this);
                }
            }

            if (other.children != null) {
                for (Ast child : other.children) {
                    child.setParent(other);
                }
            }
        }

        this.setDirtyRecursivelyToParents(true);
        other.setDirtyRecursivelyToParents(true);
    }

    private boolean isChildOf(Ast potentialParent) {
        if (getParent() == null) {
            return false;
        }
        if (getParent() == potentialParent) {
            return true;
        }

        return getParent().isChildOf(potentialParent);
    }

    public void swapContentChangeOnlyValueAndType(Ast other) {
        RuleType tempRuleType = this.type;
        this.type = other.type;
        other.type = tempRuleType;

        String tempValue = this.value;
        this.value = other.value;
        other.value = tempValue;

        this.setDirtyRecursivelyToParents(true);
        other.setDirtyRecursivelyToParents(true);
    }

    public Ast deepClone() {
        Ast newHolder = new Ast();
        newHolder.stringValue = stringValue;
        newHolder.type = type;
        newHolder.value = value;
        newHolder.parent = parent;

        if (children != null) {
            List<Ast> newChildren = new ArrayList<Ast>();
            for (int i = 0; i < children.size(); i++) {
                newChildren.add(children.get(i).deepClone());
            }

            newHolder.setChildren(newChildren);
        }

        return newHolder;
    }

    public boolean deepEquals(Ast astHolder) {
        return deepToString().equals(astHolder.deepToString());
    }

    public static AstHolderFactory numberFactory(String value) {
        return new AstHolderFactory(new Ast().setType(RuleType.NUMBER).setValue(value).setDirty(true));
    }
}
