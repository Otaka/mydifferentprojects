package com;

import com.lang.ASTCompiler;
import com.lang.expressions.Module;

/**
 * @author Dmitry
 */
public class Main {

    public static void main(String[] args) {
        Module module = new ASTCompiler().parseSource("(deff @noheader @interruptreturn main((uint a) (uchar a))[(defv v (+ a b))])");
        module.getExpressions();
    }
}
