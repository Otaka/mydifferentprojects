package com.simplecas4j;

import com.simplecas4j.ast.Ast;

/**
 * @author Dmitry
 */
public class EquationExecutor {

    public static final String VAR = "variable";
    public static final String ORDERED_OPERATOR = "orderedOperator";
    public static final String OPERATOR = "operator";
    public static final String NUMBER = "number";
    public static final String PARENTHESES = "parentheses";
    public static final String ANY = "any";
    public static final String NO_MORE = "noMore";

    public Ast var(String name) {
        return new Ast().setType(VAR).setValue(name);
    }

    public Ast op(String type, Ast... children) {
        return new Ast().setType(OPERATOR).setValue(type).setChildren(children);
    }

    public Ast number(String number) {
        return new Ast().setType(NUMBER).setValue(number);
    }

    public Ast parentheses(Ast... children) {
        return new Ast().setType(PARENTHESES).setChildren(children);
    }

    public double evaluateAst(Ast ast) {
        return 0;
    }
}
