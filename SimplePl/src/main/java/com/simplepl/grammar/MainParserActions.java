package com.simplepl.grammar;

import com.simplepl.exception.ParseException;
import com.simplepl.grammar.ast.Ast;
import java.util.Formatter;
import org.apache.commons.lang.StringUtils;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.support.Position;

/**
 * @author Dmitry
 */
public class MainParserActions extends BaseParser<Object> {

    public final static String UNKNOWN = null;
    private final String errorMessageTemplate = "[Line:%1$d Column:%2$d] %3$s. Found \"%4$s\"";
    private static boolean enableAction = true;

    public static void setEnableAction(boolean enable) {
        enableAction = enable;
    }

    public static boolean isEnableAction() {
        return enableAction;
    }

    public Action actionFail(final String message) {
        return (Action) (Context context1) -> {
            String fullMessage = message;
            if (fullMessage.contains("$match")) {
                fullMessage = fullMessage.replace("$match", context1.getMatch());
            }

            int currentIndex = context1.getCurrentIndex();
            Position position = context1.getInputBuffer().getPosition(currentIndex);
            String part = context1.getInputBuffer().extract(currentIndex, currentIndex + 20);
            part = part.replace("\n", "\\n");
            Formatter formatter = new Formatter();
            formatter.format(errorMessageTemplate, position.line, position.column, fullMessage, part);
            fullMessage = formatter.toString();
            throw new ParseException(currentIndex, fullMessage);
        };
    }

    public Action dbgPrint(final String message) {
        return (Action) (Context context1) -> {
            String m = message;
            if (m.contains("$match")) {
                m = m.replace("$match", context1.getMatch());
            }

            System.out.println(m);
            return true;
        };
    }

    public static abstract class LangAction implements Action {

        @Override
        public boolean run(Context context) {
            if (!enableAction) {
                return true;
            }

            return runAction(context);
        }

        public abstract boolean runAction(Context context);
    }

    /*
    public Action pushMatchString(final String label) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                String value = context.getMatch().trim();
                push(new StringObj(value, label));
                return true;
            }
        };
    }*/
    private String lastMatch() {
        return getContext().getMatch().trim();
    }

    public Action _pushAst_ExtractTopAstsAndSetAsAttributes(String name, String... attributeNames) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = new Ast(name);
                for (String attributeName : attributeNames) {
                    Object attributeAst = context.getValueStack().pop();
                    if (attributeAst instanceof Ast) {
                        ast.getAttributes().put(attributeName, attributeAst);
                    } else {
                        throw new IllegalArgumentException("Cannot convert object [" + attributeAst + "] to Ast class or to StringObj class.");
                    }
                }

                context.getValueStack().push(ast);
                return true;
            }
        };
    }

    public Action _pushAst_ExtractTopAstAndSetAsChild(String name) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = new Ast(name);
                Ast childAst = (Ast) context.getValueStack().pop();
                ast.getChildren().add(childAst);
                context.getValueStack().push(ast);
                return true;
            }
        };
    }

    public Action _setAttributeOnLastAst(String name, Object value) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                ((Ast) context.getValueStack().peek()).addAttribute(name, value);
                return true;
            }
        };
    }

    public Action _pushVariableToArgumentList() {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast variable = (Ast) context.getValueStack().pop();
                checkAstHasNecessaryName(variable, "var", false);
                Ast argumentList = (Ast) context.getValueStack().peek();
                checkAstHasNecessaryName(argumentList, "function_arguments", false);
                argumentList.addChild(variable);
                return true;
            }
        };
    }

    public Action _pushFunctionExtensionToDeclaration() {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast extensionArgumentList = (Ast) context.getValueStack().pop();
                checkAstHasNecessaryName(extensionArgumentList, "function_arguments", false);
                Ast function = (Ast) context.getValueStack().peek();
                checkAstHasNecessaryName(function, "function", false);
                function.addAttribute("extension", extensionArgumentList);
                return true;
            }
        };
    }

    public Action _pushAstWithMatchedStringAsAttribute(String astName, String attributeName) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = new Ast(astName);
                ast.addAttribute(attributeName, lastMatch());
                context.getValueStack().push(ast);
                return true;
            }
        };
    }

    public Action _pushTopStackAstToNextStackAstAsChild(String expectedChildName, String expectedParentName) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast child = (Ast) context.getValueStack().pop();
                if (!StringUtils.equals(expectedChildName, UNKNOWN)) {
                    checkAstHasNecessaryName(child, expectedChildName, true);
                }

                Ast parent = (Ast) context.getValueStack().peek();
                if (!StringUtils.equals(expectedParentName, UNKNOWN)) {
                    checkAstHasNecessaryName(parent, expectedParentName, false);
                }

                parent.addChild(child);
                return true;
            }
        };
    }

    public Action _pushTopStackAstToNextStackAstAsAttribute(String attributeName, String expectedChildName, String expectedParentName) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast child = (Ast) context.getValueStack().pop();
                if (!StringUtils.equals(expectedChildName, UNKNOWN)) {
                    checkAstHasNecessaryName(child, expectedChildName, true);
                }

                Ast parent = (Ast) context.getValueStack().peek();
                if (!StringUtils.equals(expectedParentName, UNKNOWN)) {
                    checkAstHasNecessaryName(parent, expectedParentName, false);
                }

                parent.addAttribute(attributeName, child);
                return true;
            }
        };
    }

    public Action _pushBinaryOperation() {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast rightExpression = (Ast) context.getValueStack().pop();

                Ast operation = (Ast) context.getValueStack().pop();
                Ast leftExpression = (Ast) context.getValueStack().pop();
                operation.addChild(leftExpression);
                operation.addChild(rightExpression);
                context.getValueStack().push(operation);
                return true;
            }
        };
    }

    public Action _pushAst(String name) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = new Ast(name);
                context.getValueStack().push(ast);
                return true;
            }
        };
    }

    public void checkAstHasNecessaryName(Ast ast, String name, boolean child) {
        if (!ast.getName().equals(name)) {
            throw new IllegalStateException("Expected " + (child ? "child" : "parent") + " '" + name + "' ast but found '" + ast.getName() + "'");
        }
    }

}
