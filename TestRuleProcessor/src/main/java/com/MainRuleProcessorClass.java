package com;

import com.simplecas4j.EquationExecutor;
import com.simplecas4j.RuleManager;
import com.simplecas4j.ast.Ast;
import com.simplecas4j.ast.AstHolder;

/**
 * @author Dmitry
 */
public class MainRuleProcessorClass {

    public static void main(String[] args) {
        System.out.println("Started rule processor");
        RuleManager ruleManager = new RuleManager();
        EquationExecutor ee = new EquationExecutor();
        AstHolder ast = ee.op("+", ee.number("6"), ee.var("x"), ee.number("67"));
        double result = ee.evaluateAst(ast);
    }
}