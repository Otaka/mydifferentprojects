package com.simplecas4j.ast;

import com.simplecas4j.BaseTest;
import com.simplecas4j.EquationExecutor;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sad
 */
public class AstHolderTest extends BaseTest {

    public AstHolderTest() {
    }

    @Test
    public void testSetContentFromXAst() {
        EquationExecutor e = new EquationExecutor();
        Ast constructedAst = null;
        Ast mul = null;
        Ast sum = null;

        mul = e.mul(e.x(), e.y());
        sum = e.sum(e.number("1"), e.number("2"));
        constructedAst = e.div(mul, sum);
        Assert.assertEquals("[[x*y]/[1+2]]", constructedAst.deepToString());
        mul.setContentFromXAstAndRemoveX(sum);
        Assert.assertEquals("[/: [1+2]]", constructedAst.deepToString());
        validateThatChildAndParentsInProperRelations(constructedAst);

        //more complex case when one element is parent of another
        mul = e.mul(e.x(), e.y());
        sum = e.sum(e.number("1"), mul);
        constructedAst = sum;
        Assert.assertEquals("[1+[x*y]]", constructedAst.deepToString());
        sum.setContentFromXAstAndRemoveX(mul);
        Assert.assertEquals("[x*y]", constructedAst.deepToString());
        validateThatChildAndParentsInProperRelations(constructedAst);
    }

    @Test
    public void testSwapContentOnlyValueAndType() {
        EquationExecutor e = new EquationExecutor();

        //first test replace independent expressions (+ some1, some2)) -> (+ some2, some1)
        Ast arg1Operator = e.op("-", e.number("1"), e.number("2"));
        Ast arg2Operator = e.op("+", e.var("x"), e.var("y"));
        Ast ast = e.sum(arg1Operator, arg2Operator);
        Assert.assertEquals("[[1-2]+[x+y]]", ast.deepToString());
        arg1Operator.swapContentChangeOnlyValueAndType(arg2Operator);
        Assert.assertEquals("[[1+2]+[x-y]]", ast.deepToString());
        validateThatChildAndParentsInProperRelations(ast);

        //some not so good example
        arg1Operator = e.op("-", e.number("56"), e.number("89"));
        Ast arg2Parenthesis = e.parentheses(e.var("x"));
        ast = e.sum(arg1Operator, arg2Parenthesis);
        Assert.assertEquals("[[56-89]+(x)]", ast.deepToString());
        arg1Operator.swapContentChangeOnlyValueAndType(arg2Parenthesis);
        Assert.assertEquals("[(56)ERR_PAR_CHILD[89]+[-: x]]", ast.deepToString());
        validateThatChildAndParentsInProperRelations(ast);

        //second test replace parent with child
        Ast var = e.op("*", e.number("1"), e.number("2"));
        Ast plus = e.sum(e.number("100"), var);
        Assert.assertEquals("[100+[1*2]]", plus.deepToString());
        plus.swapContentChangeOnlyValueAndType(var);
        Assert.assertEquals("[100*[1+2]]", plus.deepToString());
        validateThatChildAndParentsInProperRelations(plus);
    }
}
