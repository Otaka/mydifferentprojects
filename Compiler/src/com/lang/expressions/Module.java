package com.lang.expressions;

import com.lang.expressions.SExpression;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmitry
 */
public class Module {

    public List<SExpression> expressions = new ArrayList<>();

    public Module(List<SExpression> expressions) {
        this.expressions.addAll(expressions);
    }

    public List<SExpression> getExpressions() {
        return expressions;
    }

}
