package com.expressionlayout.interpreter;

import java.util.List;

/**
 * @author sad
 */
public class CompiledSubExpression {

    private Token assignTo;
    private List<Token> polishNotationTokens;

    public CompiledSubExpression(Token assignTo, List<Token> polishNotationTokens) {
        this.assignTo = assignTo;
        this.polishNotationTokens = polishNotationTokens;
    }

    public Token getAssignTo() {
        return assignTo;
    }

    public List<Token> getPolishNotationTokens() {
        return polishNotationTokens;
    }

}
