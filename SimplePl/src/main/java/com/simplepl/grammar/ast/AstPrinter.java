package com.simplepl.grammar.ast;

import org.apache.commons.lang.StringUtils;

/**
 * @author sad
 */
public class AstPrinter {
    private int tab=4;
    public void printAstTree(Ast ast) {
        printAstTree(ast, 0, false);
    }

    private void pad(int level) {
        System.out.print(StringUtils.repeat(" ", level));
    }

    private void printWithPadding(String value, int level) {
        pad(level);
        System.out.print(value);
    }

    private void printAstTree(Ast ast, int level, boolean firstPadding) {
        if(firstPadding){
            printWithPadding("{\n", level);
        }else{
            System.out.print("{\n");
        }
        level+=tab;
        printWithPadding("\"n\":\"" + ast.getName() + "\",\n", level);
        if (!ast.getAttributes().isEmpty()) {
            printWithPadding("\"attr\":{\n", level);
            level = level + tab;
            boolean first = true;
            for (String key : ast.getAttributes().keySet()) {
                Object value = ast.getAttributes().get(key);
                if (first == false) {
                    System.out.print(", \n");
                }

                printWithPadding("\"" + key + "\":", level);
                if (value instanceof String) {
                    System.out.print(" \"" + value + "\"");
                } else if (value instanceof Ast) {
                    printAstTree((Ast) value, level + tab,false);
                } else {
                    throw new IllegalArgumentException("Cannot print value of type " + value.getClass().getSimpleName());
                }

                first = false;
            }

            level = level - tab;
            System.out.println();
            printWithPadding("}\n", level);
        }

        if (!ast.getChildren().isEmpty()) {
            printWithPadding("\"chldr\":[", level);
            boolean first = true;
            for (Ast child : ast.getChildren()) {
                if (first == false) {
                    System.out.print(",\n");
                }
                printAstTree(child, level + tab, !first);
                first = false;
            }
            System.out.println();
            printWithPadding("]\n", level);
        }

        level-=tab;
        printWithPadding("}", level);
    }
}
