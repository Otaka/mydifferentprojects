package com.simplepl.ast;

import com.simplepl.BaseTest;
import java.io.IOException;
import org.junit.Test;

public class AstTestTest extends BaseTest {

    private static String AST_FILE_NAME = "astTestData";
    private static String ARRAY_FILE_NAME = "arrayTestData";

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
    public void testFunctionInStructure() throws IOException {
        testAstExpressionFromFile("str.myfunction(1)", AST_FILE_NAME, "functionInStructure");
    }

    @Test
    public void testFunctionInStructureExtractStructure() throws IOException {
        testAstExpressionFromFile("str.myfunction(1).length", AST_FILE_NAME, "functionInStructureExtractStructure");
    }

    @Test
    public void testStructureInArray() throws IOException {
        testAstExpressionFromFile("str[0].asd", AST_FILE_NAME, "structureInArray");
    }

    @Test
    public void testArrayInStructure() throws IOException {
        testAstExpressionFromFile("str.asd[3]", AST_FILE_NAME, "arrayInStructure");
    }

    @Test
    public void testFunctionDeclaration() throws IOException {
        testAstExpressionFromFile("fun main(int a, int b){int c=a;}", AST_FILE_NAME, "functionDeclaration");
    }

    @Test
    public void testIf() throws IOException {
        testAstExpressionFromFile("if(a==2){print(\"qwerty\");}", AST_FILE_NAME, "if");
    }

    @Test
    public void testIfElse() throws IOException {
        testAstExpressionFromFile("if(a==2){print(\"qwerty\");}else{ x=3; }", AST_FILE_NAME, "ifElse");
    }

    @Test
    public void testIfElseIf() throws IOException {
        testAstExpressionFromFile("if(a==2){print(\"qwerty\");}else if(b==3){ x=4; }", AST_FILE_NAME, "ifElseIf");
    }

    @Test
    public void testExtensionFunction() throws IOException {
        testAstExpressionFromFile("myFunc(1,2){print(a);}", AST_FILE_NAME, "functionCallWithExtension");
    }

    @Test
    public void testStructure() throws IOException {
        testAstExpressionFromFile("structure mystructure{int x; int y;}", AST_FILE_NAME, "structure");
    }

    @Test
    public void testCast() throws IOException {
        testAstExpressionFromFile("x= <float>(y)", AST_FILE_NAME, "cast");
        testAstExpressionFromFile("x= <float>(sum(1,2))", AST_FILE_NAME, "castFunction");
        testAstExpressionFromFile("x= 1.0 + <float>(sum(1,2))", AST_FILE_NAME, "cast_binary_op");
    }

    @Test
    public void testNot() throws IOException {
        testAstExpressionFromFile("x= !x", AST_FILE_NAME, "notVariable");
    }

    @Test
    public void testNotInParenthesis() throws IOException {
        testAstExpressionFromFile("x= (!x)+(9*!x)", AST_FILE_NAME, "notVariableComplex");
    }

    @Test
    public void testArraySimpleGet() throws IOException {
        testAstExpressionFromFile("x= myarray[1]", ARRAY_FILE_NAME, "arraySimpleGetNumber");
        testAstExpressionFromFile("x=2* myarray[1]", ARRAY_FILE_NAME, "arraySimpleGet");
    }

    @Test
    public void testArrayGetExpression() throws IOException {
        testAstExpressionFromFile("x= myarray[myfunction(45)]", ARRAY_FILE_NAME, "arrayGetExpression");
        testAstExpressionFromFile("x= myarray[<int>(myfunction(45))]", ARRAY_FILE_NAME, "arrayGetExpressionWithCast");
    }

    @Test
    public void testArrayGetFromResult() throws IOException {
        testAstExpressionFromFile("myfunction(1)[45]", ARRAY_FILE_NAME, "arrayGetFromResult");
    }

    @Test
    public void testArrayMultidimensional() throws IOException {
        testAstExpressionFromFile("x=myarray[45][65]", ARRAY_FILE_NAME, "arrayMultidimensional");
    }

    @Test
    public void testArrayMultidimensionalWithStructure() throws IOException {
        testAstExpressionFromFile("x=myarray[45][65].length", ARRAY_FILE_NAME, "arrayMultidimensionalWithStructure");
    }

    @Test
    public void testArrayArgumentOfFunction() throws IOException {
        testAstExpressionFromFile("myfunction(a[3])", ARRAY_FILE_NAME, "arrayArgumentOfFunction");
    }
}