package com.simplecas4j;

import com.simplecas4j.ast.Ast;
import com.simplecas4j.ast.AstHolder;

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

    public AstHolder var(String name) {
        return new AstHolder(new Ast().setType(VAR).setValue(name));
    }

    public AstHolder op(String type, AstHolder... children) {
        return new AstHolder( new Ast().setType(OPERATOR).setValue(type).setChildren(children));
    }

    public AstHolder number(String number) {
        return new AstHolder(new Ast().setType(NUMBER).setValue(number));
    }

    public AstHolder parentheses(AstHolder... children) {
        return new AstHolder( new Ast().setType(PARENTHESES).setChildren(children));
    }

    public double evaluateAst(AstHolder ast) {
        return 0;
    }
}