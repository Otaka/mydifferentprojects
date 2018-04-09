package com;

import com.simplecas4j.EquationExecutor;
import com.simplecas4j.ast.Ast;

/**
 * @author Dmitry
 */
public class MainRuleProcessorClass {

    public static void main(String[] args) {
        System.out.println("Started rule processor");
        EquationExecutor ee = new EquationExecutor();

        Ast ast = ee.op("/", ee.number("6"),ee.number("3"));
        //AstHolder ast = ee.op("+", ee.number("6"), ee.var("x"), ee.number("67"));
        System.out.println("before change: " + ast.deepToString());
        ee.evaluateAst(ast);
        System.out.println("after change:  " + ast.deepToString());
    }
}