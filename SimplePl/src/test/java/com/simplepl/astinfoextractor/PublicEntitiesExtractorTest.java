package com.simplepl.astinfoextractor;

import com.simplepl.entity.FileInfo;
import com.simplepl.BaseTest;
import com.simplepl.grammar.MainParser;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.grammar.comments.CommentRemover;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Ignore;
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
        FileInfo fileInfo = parseValue("singleFunction.spl");
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnValue().getName());
        Assert.assertEquals("inline", fileInfo.getFunctionList().get(0).getAnnotations().get(0).getValue());
        Assert.assertEquals(false, fileInfo.getFunctionList().get(0).getReturnValue().isPointer());
    }

    @Test
    public void testSingleFunctionReturnPointer() throws IOException {
        FileInfo fileInfo = parseValue("singleFunctionReturnPointer.spl");
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnValue().getName());
        Assert.assertEquals(true, fileInfo.getFunctionList().get(0).getReturnValue().isPointer());
    }
    
    @Test
    public void testSingleFunctionWithStructureGlobalVarsAndTypedef() throws IOException {
        FileInfo fileInfo = parseValue("singleFunctionWithStructureGlobalVarsAndTypedef.spl");
        /*Assert.assertEquals("main", fileInfo.functionList.get(0).getName());
        Assert.assertEquals("int", fileInfo.functionList.get(0).getReturnValue().getName());
        Assert.assertEquals("inline", fileInfo.functionList.get(0).getAnnotations().get(0).getValue());
        Assert.assertEquals(false, fileInfo.functionList.get(0).getReturnValue().isPointer());*/
    }

    private FileInfo parseValue(String fileName) throws IOException {
        Ast ast = parseTestFile(fileName);
        PublicEntitiesExtractor extractor = new PublicEntitiesExtractor();
        return extractor.processAst(ast);
    }

    private Ast parseTestFile(String testFile) throws IOException {
        String sourceText = loadFile(testFile);
        CommentRemover commentRemover=new CommentRemover(sourceText);
        sourceText=commentRemover.process();
        MainParser parser = createParser();
        ParseRunner runner = new BasicParseRunner(parser.main());
        ParsingResult<Object> articleResult = runner.run(sourceText);
        if(!articleResult.matched){
            Assert.fail("Cannot compile source");
        }
        Ast ast = (Ast) articleResult.valueStack.pop();
        if(!articleResult.valueStack.isEmpty()){
            throw  new IllegalStateException("Value stack at the end should contain only one value");
        }
        return ast;
    }

}
