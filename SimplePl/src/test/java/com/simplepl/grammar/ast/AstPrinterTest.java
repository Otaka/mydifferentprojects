package com.simplepl.grammar.ast;

import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class AstPrinterTest {

    public AstPrinterTest() {
    }

    @Test
    public void testPrintAstTree() {
       
        Ast ast = new Ast("myast");
        ast.addAttribute("myattribute", new Ast("attribute"));
        ast.addChild(new Ast("child"));
        AstPrinter instance = new AstPrinter();
        instance.printAstTree(ast);
        
    }

}