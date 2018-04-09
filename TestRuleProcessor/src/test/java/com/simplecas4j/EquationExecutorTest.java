package com.simplecas4j;

import com.simplecas4j.ast.Ast;
import org.junit.Test;

/**
 *
 * @author sad
 */
public class EquationExecutorTest extends BaseTest {

    @Test
    public void testExpression() {
        EquationExecutor e = new EquationExecutor();

        Ast ast = null;

        ast = e.sum(e.x(), e.number("98"));
        e.evaluateAst(ast);
        checkNum(ast.getChildren().get(0), "98");
        checkIsX(ast.getChildren().get(1));
        validateThatChildAndParentsInProperRelations(ast);

        //(/ num1,num2)->dividedNumber
        ast = e.op("/", e.number("6"), e.number("2"));
        e.evaluateAst(ast);
        checkNum(ast, "3");
        validateThatChildAndParentsInProperRelations(ast);

        //(+ num,num)->sum
        ast = e.sum(e.number("9"), e.number("2"));
        e.evaluateAst(ast);
        checkNum(ast, "11");
        validateThatChildAndParentsInProperRelations(ast);

        //(* num, num)->mul
        ast = e.op("*", e.number("2"), e.number("4"));
        e.evaluateAst(ast);
        checkNum(ast, "8");
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.op("*", e.number("2"));
        e.evaluateAst(ast);
        checkNum(ast, "2");
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.sum(e.number("2"));
        e.evaluateAst(ast);
        checkNum(ast, "2");
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.sum(e.number("0"), e.x());
        e.evaluateAst(ast);
        checkIsX(ast);
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.sum(e.x(), e.number("0"));
        e.evaluateAst(ast);
        checkIsX(ast);
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.sum(e.y(), e.sum(e.x(), e.number("98")));
        e.evaluateAst(ast);
        checkNum(ast.getChildren().get(0), "98");
        checkIsY(ast.getChildren().get(1));
        checkIsX(ast.getChildren().get(2));
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.mul(e.number("0"), e.y());
        e.evaluateAst(ast);
        checkNum(ast, "0");
        validateThatChildAndParentsInProperRelations(ast);

        ast = e.mul(e.y(), e.number("0"));
        e.evaluateAst(ast);
        checkNum(ast, "0");
        validateThatChildAndParentsInProperRelations(ast);

        //(+ (anyOperation), num)->(+ num,(anyOperation))
        ast = e.sum(e.parentheses(e.mul(e.x(), e.number("100"))), e.number("67"));
        e.evaluateAst(ast);
        checkNum(ast.getChildren().get(0), "67");
        checkIsPar(ast.getChildren().get(1));
        validateThatChildAndParentsInProperRelations(ast);

        //(* 0) -> 0
        ast = e.mul(e.number("98"));
        e.evaluateAst(ast);
        checkNum(ast, "98");
        validateThatChildAndParentsInProperRelations(ast);

        //(* num) -> num
        ast = e.parentheses(e.number("56"));
        e.evaluateAst(ast);
        checkNum(ast, "56");
        validateThatChildAndParentsInProperRelations(ast);

        //(* var) -> var
        ast = e.parentheses(e.x());
        e.evaluateAst(ast);
        checkIsX(ast);
        validateThatChildAndParentsInProperRelations(ast);

        //(/ num, num) -> dividedNum
        ast = e.div(e.number("68"), e.number("2"));
        e.evaluateAst(ast);
        checkNum(ast, "34");
        validateThatChildAndParentsInProperRelations(ast);

        //(/ any1,any1)->1
        ast = e.div(e.x(), e.x());
        e.evaluateAst(ast);
        checkNum(ast, "1");
        validateThatChildAndParentsInProperRelations(ast);

        //(/ any1,1)->any
        ast = e.div(e.x(), e.number("1"));
        e.evaluateAst(ast);
        checkIsX(ast);
        validateThatChildAndParentsInProperRelations(ast);

        // (/ any1, (* any1, any2))->(* 1/any2)
        ast = e.div(e.x(), e.mul(e.x(), e.y()));
        e.evaluateAst(ast);
        checkNum(ast.getChildren().get(0), "1");
        checkIsY(ast.getChildren().get(1));

        // (/ (* any1, any2),any1)->(* any2)
        ast = e.div(e.mul(e.x(), e.y()), e.x());
        e.evaluateAst(ast);
        checkIsY(ast);

        ast=e.sum(e.number("67"),e.div(e.y(), e.number("1")),e.mul(e.x(),e.x(),e.number("1")));
        System.out.println(ast.deepToString());
        e.evaluateAst(ast);
        System.out.println(ast.deepToString());
    }
}