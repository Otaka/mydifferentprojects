package com.simplepl.grammar;

import com.simplepl.BaseTest;
import java.io.IOException;
import org.junit.Test;

public class MainParserTest extends BaseTest {

    @Test
    public void testMainFunction() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testFunctionRule(), "mainFunction");
    }

    @Test
    public void testExtensionFunction() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "extensionFunction");
    }

    @Test
    public void testStructure() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testStructure(), "structure");
    }

    @Test
    public void testStructureIntegrationTest() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "functionWithStructure");
    }

    @Test
    public void testInnerFunction() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "innerFunctions");
    }

    @Test
    public void testExtensionFunctionWithInnerCall() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "extensionFunctionWithInnerCall");
    }

    @Test
    public void testSimplePointers() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.testFunctionRule(), "NewDelete");
    }

    @Test
    public void testIf() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "if");
    }

    @Test
    public void testIfElse() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "ifElse");
    }

    @Test
    public void testIfElseIf() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "ifElseIf");
    }

    @Test
    public void testFor() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "forLoop");
    }

    @Test
    public void testWhile() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "whileLoop");
    }

    @Test
    public void testStructureWithPointer() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "structureWithPointer");
    }

    @Test
    public void testPointers() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "pointers");
    }

    @Test
    public void testCast() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "cast");
    }

    @Test
    public void testBigTest() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "bigTest");
    }

    @Test
    public void testArithmeticTest() throws IOException {
        MainParser parser = createParser();
        checkFileRuleSuccess(parser, parser.main(), "arithmetic");
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
