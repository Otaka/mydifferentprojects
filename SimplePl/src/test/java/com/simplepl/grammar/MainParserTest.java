package com.simplepl.grammar;

import java.io.IOException;
import org.junit.Test;

public class MainParserTest extends BaseTest {

    @Test
    public void testMainFunction() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testFunctionRule(), "mainFunction");
    }

    //@Test
    public void testExtensionFunction() throws IOException {
        // TODO implement extension function
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testFunctionRule(), "extensionFunction");
    }

    //@Test
    public void testInnerFunction() throws IOException {
        // TODO implement inner function
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testFunctionRule(), "innerFunction");
    }

    @Test
    public void testVariableDeclaration() throws IOException {
        MainParser parser = createParser();
        checkTextRuleSuccess(parser, parser.testDeclareVariable(), " int age");
        checkTextRuleSuccess(parser, parser.testDeclareVariable(), "int  age ");
        checkTextRuleFailure(parser, parser.testDeclareVariable(), "intage");
    }

    @Test
    public void testFunctionCall() throws IOException {
        MainParser parser = createParser();
        checkTextRuleSuccess(parser, parser.testFunctionCall(), "print ( ) ");

        checkTextRuleFailure(parser, parser.testFunctionCall(), "print )");
        checkTextRuleFailure(parser, parser.testFunctionCall(), "print(");
        checkTextRuleSuccess(parser, parser.testFunctionCall(), "print(1)");
        checkTextRuleSuccess(parser, parser.testFunctionCall(), "print(1+5)");
        checkTextRuleSuccess(parser, parser.testFunctionCall(), "print(1+5,x)");
    }

    @Test
    public void testArrayDeclaration() throws IOException {
        MainParser parser = createParser();
        checkTextRuleSuccess(parser, parser.testDeclareArray(), "int[ ]age ");
        checkTextRuleSuccess(parser, parser.testDeclareArray(), " int [][ ] age");

        checkTextRuleFailure(parser, parser.testDeclareArray(), "intage");
        checkTextRuleFailure(parser, parser.testDeclareArray(), "int[age");
        checkTextRuleFailure(parser, parser.testDeclareArray(), "int]age");
        checkTextRuleFailure(parser, parser.testDeclareArray(), "int[sdd]age");
        checkTextRuleFailure(parser, parser.testDeclareArray(), "int[6]age");
    }

}
