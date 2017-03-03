package com.simplepl.ast;

import com.simplepl.BaseTest;
import com.simplepl.grammar.MainParser;
import java.io.IOException;
import org.junit.Test;

public class AstTestTest extends BaseTest {

    private static String AST_FILE_NAME = "astTestData";

    @Test
    public void testDigit() throws IOException {
        testAstExpressionFromFile("5", AST_FILE_NAME, "digitTest");
    }

    @Test
    public void testPlus() throws IOException {
        testAst("5+9", "{'n':'binary_operation','attr':{'operation':'+'}, 'chldr':["
                + "{'attr':{'value':'5'}},"
                + " {'attr':{'value':'9'}}"
                + "]}");
    }

    @Test
    public void testMultiple() throws IOException {
        testAstExpressionFromFile("15*29", AST_FILE_NAME, "multipleTest");
    }

    @Test
    public void testDivide() throws IOException {
        testAst("15/(29)", "{'n':'binary_operation','attr':{'operation':'/'}, 'chldr':["
                + "{'attr':{'value':'15'}},"
                + " {'attr':{'value':'29'}}"
                + "]}");
    }

    @Test
    public void testComplex() throws IOException {
        testAstExpressionFromFile("(2+6)/(1*3)", AST_FILE_NAME, "parenthesis");
    }

    @Test
    public void testAssignVariable() throws IOException {
        testAstExpressionFromFile("double r=(a-b)/(b-a)", AST_FILE_NAME, "varAssign");
    }

    @Test
    public void testFunctionCall() throws IOException {
        testAstExpressionFromFile("myfunction(1,2)", AST_FILE_NAME, "functionCall");
    }

    @Test
    public void testPointerDeclaration() throws IOException {
        testAstExpressionFromFile("int@ intPointer", AST_FILE_NAME, "pointerDeclaration");
    }

    @Test
    public void testPointerDeclarationAndAssignment() throws IOException {
        testAstExpressionFromFile("int@ intPointer=new int", AST_FILE_NAME, "pointerDeclarationAndAssignment");
    }

    @Test
    public void testPointerAssignment() throws IOException {
        testAstExpressionFromFile("@intPointer = 43", AST_FILE_NAME, "pointerAssignment");
    }

    @Test
    public void testForLoop() throws IOException {
        testAstExpressionFromFile("for(int i=0;i<45;i=i+1){print(\"hello\");}", AST_FILE_NAME, "for_loop");
    }

    @Test
    public void testStructureFieldAssign() throws IOException {
        testAstExpressionFromFile("str.buffer.length", AST_FILE_NAME, "structureFieldAssign");
    }

    @Test
    public void testFunctionDeclaration() throws IOException {
        testAstExpressionFromFile("fun main(int a, int b){int c=a;}", AST_FILE_NAME, "functionDeclaration");
    }
}
