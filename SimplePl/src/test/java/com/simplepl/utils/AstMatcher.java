package com.simplepl.utils;

import com.simplepl.grammar.ast.Ast;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author sad
 */
public class AstMatcher implements AstValue{

    private String n;
    private Map<String, AstValue> attr;
    private List<AstMatcher> chldr;

    public AstMatcher() {
    }

    public AstMatcher(String name) {
        this.n = name;
    }

    public void setAttributes(Map<String, AstValue> attributes) {
        this.attr = attributes;
    }

    public void setChildren(List<AstMatcher> children) {
        this.chldr = children;
    }

    public Map<String, AstValue> getAttributes() {
        return attr;
    }

    public List<AstMatcher> getChildren() {
        return chldr;
    }

    public String getName() {
        return n;
    }

    public void setName(String name) {
        this.n = name;
    }

    public boolean match(Ast ast) {
        if (getName() != null) {
            if (!getName().equals(ast.getName())) {
                throw new IllegalArgumentException("Expected ast with name '" + getName() + "' but found '" + ast.getName() + "'");
            }
        }

        if (getAttributes() != null) {
            for (String attributeName : getAttributes().keySet()) {
                if (!ast.getAttributes().containsKey(attributeName)) {
                    throw new IllegalArgumentException("Ast '" + ast.getName() + "' should contain attribute '" + attributeName + "' = [" + getAttributes().get(attributeName) + "]");
                }

                Object astAttribValue = ast.getAttributes().get(attributeName);
                if (astAttribValue instanceof String) {
                    StringValue attributeStringValue = (StringValue) getAttributes().get(attributeName);
                    String strValue=null;
                    if(attributeStringValue!=null){
                        strValue=attributeStringValue.getStrValue();
                    }
                    if (!Objects.equals(strValue, astAttribValue)) {
                        throw new IllegalArgumentException("Expected ast attribute '" + attributeName + "' with value '" + strValue + "' but found with value '" + astAttribValue + "'");
                    }
                } else if (astAttribValue instanceof Ast) {
                    Ast astAttributeAst=(Ast) astAttribValue;
                    AstMatcher attributeValueAst = (AstMatcher) getAttributes().get(attributeName);
                    attributeValueAst.match(astAttributeAst);
                } else {
                    throw new IllegalStateException("Attribute value match is not implemented for class [" + astAttribValue.getClass().getSimpleName() + "]");
                }
            }
        }

        if (getChildren() != null) {
            if (ast.getChildren() == null) {
                throw new IllegalArgumentException("Ast '" + getName() + "' should contain children, but it contains null");
            }

            if (ast.getChildren().size() != getChildren().size()) {
                throw new IllegalArgumentException("Ast '" + getName() + "' should contain " + getChildren().size() + " children, but it contains " + ast.getChildren().size());
            }

            for (int i = 0; i < getChildren().size(); i++) {
                AstMatcher childMatcher = getChildren().get(i);
                Ast childAst = ast.getChildren().get(i);
                childMatcher.match(childAst);
            }
        }

        return false;
    }
}
