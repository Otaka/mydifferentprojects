package com.expressionlayout.interpreter;

import java.util.List;

/**
 * @author sad
 */
public class CompiledExpression {

    private List<CompiledSubExpression> subexpressions;

    public CompiledExpression(List<CompiledSubExpression> subexpressions) {
        this.subexpressions = subexpressions;
    }

    public List<CompiledSubExpression> getSubexpressions() {
        return subexpressions;
    }

}
