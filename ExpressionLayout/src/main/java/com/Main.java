package com;

import com.expressionlayout.interpreter.CompiledExpression;
import com.expressionlayout.interpreter.Interpreter;
import com.expressionlayout.interpreter.NumberObject;
import com.expressionlayout.interpreter.VariableResolver;


/**
 * @author sad
 */
public class Main {

    public static void main(String[] args) {
        testPolishNotationInterpreter();
    }

    private static void testPolishNotationInterpreter() {
        Interpreter interpreter = new Interpreter();
        interpreter.addVariableResolver(new VariableResolver() {
            @Override
            public NumberObject getVariable(String variableName) {
                switch (variableName) {
                    case "x":
                        return new NumberObject(90);
                    case "y":
                        return new NumberObject(5);
                }

                return null;
            }

            @Override
            public NumberObject getVariableField(String variableName, String field) {
                switch (variableName) {
                    case "mycomponent":
                        if (field.equals("x")) {
                            return new NumberObject(4);
                        }

                        throw new IllegalArgumentException("mycomponent does not have field [" + field + "]");
                }

                return null;
            }

            @Override
            public boolean setVariable(String variableName, NumberObject no) {
                if (variableName.equals("width")) {
                    System.out.println("Variable [" + variableName + "] = " + no.getAsLong());
                    return true;
                }

                return false;
            }

            @Override
            public boolean setVariableField(String variableName, String field, NumberObject no) {
                if (variableName.equals("width")) {
                    if (field.equals("p")) {
                        System.out.println("Variable [" + variableName + "] field [" + field + "] = " + no.getAsLong());
                        return true;
                    }
                }

                return false;
            }

            @Override
            public NumberObject suffixResolver(NumberObject numberObject) {
                if (numberObject.getSuffix() == null) {
                    return numberObject;
                }

                if (numberObject.getSuffix().equals("pt")) {
                    return numberObject.makeClone().set(numberObject.getAsLong() / 2).clearSuffix();
                }

                return null;
            }
        });

        CompiledExpression expression = interpreter.compileExpression("width.p=50pt+100pt");
        System.out.println("Result=" + interpreter.execute(expression));
    }
}
