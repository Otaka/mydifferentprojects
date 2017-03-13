package com.simplepl.astinfoextractor;

import com.simplepl.entity.FileInfo;
import com.simplepl.BaseTest;
import com.simplepl.entity.Structure;
import com.simplepl.grammar.MainParser;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.grammar.comments.CommentRemover;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author sad
 */
public class PublicEntitiesExtractorTest extends BaseTest {

    public PublicEntitiesExtractorTest() {
        setBasePath("/com/simplepl/astinfoextractor/testdata/");
    }

    @Test
    public void testSingleFunction() throws IOException {
        FileInfo fileInfo = parseValue("singleFunction.spl", "com.test");
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnValue().getName());
        Assert.assertEquals("inline", fileInfo.getFunctionList().get(0).getAnnotations().get(0).getValue());
        Assert.assertEquals(false, fileInfo.getFunctionList().get(0).getReturnValue().isPointer());
    }

    @Test
    public void testSingleFunctionReturnPointer() throws IOException {
        FileInfo fileInfo = parseValue("singleFunctionReturnPointer.spl", "com.test");
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnValue().getName());
        Assert.assertEquals(true, fileInfo.getFunctionList().get(0).getReturnValue().isPointer());
    }

    @Test
    public void testSingleFunctionWithStructureGlobalVarsAndTypedef() throws IOException {
        FileInfo fileInfo = parseValue("singleFunctionWithStructureGlobalVarsAndTypedef.spl", "com.test");
        //function
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnValue().getName());
        Assert.assertEquals(false, fileInfo.getFunctionList().get(0).getReturnValue().isPointer());

        //structures
        Assert.assertEquals(1, fileInfo.getStructures().size());
        Structure s = fileInfo.getStructures().get(0);
        Assert.assertEquals("TestStructure", s.getName());
        Assert.assertEquals(4, s.getFields().size());
        Assert.assertEquals("x", s.getFields().get(0).getName());
        Assert.assertEquals("int", s.getFields().get(0).getType().getName());
        Assert.assertEquals(false, s.getFields().get(0).getType().isPointer());
        Assert.assertEquals("int", s.getFields().get(3).getType().getName());
        Assert.assertEquals("somepointer", s.getFields().get(3).getName());
        Assert.assertEquals(true, s.getFields().get(3).getType().isPointer());

        //package
        Assert.assertEquals("com.test", fileInfo.getPackagePath());

        //global vars
        Assert.assertEquals(2, fileInfo.getGlobalVariables().size());
        Assert.assertEquals("element", fileInfo.getGlobalVariables().get(0).getName());
        Assert.assertEquals("int", fileInfo.getGlobalVariables().get(0).getType().getName());
        Assert.assertEquals("a", fileInfo.getGlobalVariables().get(1).getName());
        Assert.assertEquals("int", fileInfo.getGlobalVariables().get(1).getType().getName());

        //type
        Assert.assertEquals(2, fileInfo.getTypes().size());
        Assert.assertEquals("com.test.yearpointer", fileInfo.getTypes().get(0).getFullPath());
        Assert.assertEquals("yearpointer", fileInfo.getTypes().get(0).getName());
        Assert.assertEquals("int", fileInfo.getTypes().get(0).getParent().getName());
        Assert.assertEquals(true, fileInfo.getTypes().get(0).getParent().isPointer());

        Assert.assertEquals("com.test.year", fileInfo.getTypes().get(1).getFullPath());
        Assert.assertEquals("year", fileInfo.getTypes().get(1).getName());
        Assert.assertEquals("int", fileInfo.getTypes().get(1).getParent().getName());
        Assert.assertEquals(false, fileInfo.getTypes().get(1).getParent().isPointer());
    }

    private FileInfo parseValue(String fileName, String packagePath) throws IOException {
        Ast ast = parseTestFile(fileName);
        PublicEntitiesExtractor extractor = new PublicEntitiesExtractor();
        return extractor.processAst(ast, packagePath);
    }

    private Ast parseTestFile(String testFile) throws IOException {
        String sourceText = loadFile(testFile);
        CommentRemover commentRemover = new CommentRemover(sourceText);
        sourceText = commentRemover.process();
        MainParser parser = createParser();
        ParseRunner runner = new BasicParseRunner(parser.main());
        ParsingResult<Object> articleResult = runner.run(sourceText);
        if (!articleResult.matched) {
            Assert.fail("Cannot compile source");
        }
        Ast ast = (Ast) articleResult.valueStack.pop();
        if (!articleResult.valueStack.isEmpty()) {
            throw new IllegalStateException("Value stack at the end should contain only one value");
        }
        return ast;
    }

}
