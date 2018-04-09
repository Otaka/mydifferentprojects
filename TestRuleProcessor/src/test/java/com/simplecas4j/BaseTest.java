package com.simplecas4j;

import com.simplecas4j.ast.Ast;
import com.simplecas4j.rule.RuleType;
import org.junit.Assert;

/**
 * @author sad
 */
public abstract class BaseTest {
 public void checkIsPar(Ast ast){
        Assert.assertEquals(RuleType.PARENTHESES, ast.getType());
        Assert.assertEquals(1, ast.getChildren().size());
    }

    public void checkNum(Ast ast, String value) {
        Assert.assertEquals(RuleType.NUMBER, ast.getType());
        Assert.assertEquals(value, ast.deepToString());
        if (ast.getChildren() != null && !ast.getChildren().isEmpty()) {
            Assert.fail("number ast should not have any children");
        }
    }

    public void checkIsX(Ast ast){
        checkVar(ast, "x");
    }
    
    public void checkIsY(Ast ast){
        checkVar(ast, "y");
    }
    
    public void checkVar(Ast ast, String value) {
        Assert.assertEquals(RuleType.VAR, ast.getType());
        Assert.assertEquals(value, ast.deepToString());
        if (ast.getChildren() != null && !ast.getChildren().isEmpty()) {
            Assert.fail("variable ast should not have any children");
        }
    }
    
    public void validateThatChildAndParentsInProperRelations(Ast astHolder) {
        if (astHolder.getChildren() != null) {
            for (Ast child : astHolder.getChildren()) {
                if (child.getParent() != astHolder) {
                    Assert.fail("Child wrong parent");
                }
                validateThatChildAndParentsInProperRelations(child);
            }
        }
    }
}
