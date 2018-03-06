package com.simplecas4j;

import com.simplecas4j.ast.Ast;
import com.simplecas4j.rule.Rule;
import com.simplecas4j.rule.RuleReplacement;

/**
 * @author Dmitry
 */
public class MainRuleProcessorClass {

    public static void main(String[] args) {
        System.out.println("Started rule processor");
        RuleManager ruleManager=new RuleManager();
        EquationExecutor a=new EquationExecutor();
        Ast ast=a.op("+", a.number("6"),a.var("x"));
        double result=a.evaluateAst(ast);
    }
}
