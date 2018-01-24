package com.simplepl.tests;

import com.simplepl.entity.ModuleInfo;
import com.simplepl.BaseTest;
import com.simplepl.astinfoextractor.PublicEntitiesExtractor;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.StructureInfo;
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
 * @author sad
 */
public class PublicEntitiesExtractorTest extends BaseTest {

    public PublicEntitiesExtractorTest() {
        setBasePath("/com/simplepl/tests/com/test/");
    }

    @Test
    public void testSingleFunction() throws IOException {
        ModuleInfo fileInfo = parseValue("singleFunction.spl", "com.test");
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        FunctionInfo f = fileInfo.getFunctionList().get(0);
        Assert.assertEquals(2, f.getArguments().size());
        Assert.assertEquals("args", f.getArguments().get(0).getName());
        Assert.assertEquals("string", f.getArguments().get(0).getType().getTypeName());

        Assert.assertEquals("output", f.getArguments().get(1).getName());
        Assert.assertEquals("string", f.getArguments().get(1).getType().getTypeName());
        Assert.assertEquals(true, f.getArguments().get(1).getType().isPointer());

        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnType().getTypeName());
        Assert.assertEquals(false, fileInfo.getFunctionList().get(0).getReturnType().isPointer());

        Assert.assertEquals(1, fileInfo.getFunctionList().size());
    }

    @Test
    public void testSingleFunctionReturnPointer() throws IOException {
        ModuleInfo fileInfo = parseValue("singleFunctionReturnPointer.spl", "com.test");
        Assert.assertEquals(1, fileInfo.getFunctionList().size());
        Assert.assertEquals("main", fileInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getFunctionList().get(0).getReturnType().getTypeName());
        Assert.assertEquals(true, fileInfo.getFunctionList().get(0).getReturnType().isPointer());
    }

    @Test
    public void testSingleFunctionWithStructureGlobalVarsAndTypedef() throws IOException {
        ModuleInfo fileInfo = parseValue("singleFunctionWithStructureGlobalVarsAndTypedef.spl", "com.test");
        //function
        FunctionInfo f = fileInfo.getFunctionList().get(0);
        Assert.assertEquals("main", f.getName());
        Assert.assertEquals("int", f.getReturnType().getTypeName());
        Assert.assertEquals(false, f.getReturnType().isPointer());

        //structures
        Assert.assertEquals(1, fileInfo.getStructuresList().size());
        StructureInfo s = fileInfo.getStructuresList().get(0);
        Assert.assertEquals("TestStructure", s.getName());
        Assert.assertEquals(4, s.getFields().size());

        Assert.assertEquals("x", s.getFields().get(0).getName());
        Assert.assertEquals("int", s.getFields().get(0).getType().getTypeName());
        Assert.assertEquals(false, s.getFields().get(0).getType().isPointer());

        Assert.assertEquals("y", s.getFields().get(1).getName());
        Assert.assertEquals("int", s.getFields().get(1).getType().getTypeName());
        Assert.assertEquals(false, s.getFields().get(1).getType().isPointer());

        Assert.assertEquals("int", s.getFields().get(3).getType().getTypeName());
        Assert.assertEquals("somepointer", s.getFields().get(3).getName());
        Assert.assertEquals(true, s.getFields().get(3).getType().isPointer());

        //package
        Assert.assertEquals("com.test", fileInfo.getModule());

        //imports        
        Assert.assertEquals(1, fileInfo.getImports().size());

        Assert.assertEquals("com.test.utils", fileInfo.getImports().get(0).getPath());
        Assert.assertEquals(true, fileInfo.getImports().get(0).isStatic());

        //def types
        Assert.assertEquals(4, fileInfo.getDeftypesList().size());
        Assert.assertEquals("yearpointer", fileInfo.getDeftypesList().get(0).getName());
        Assert.assertEquals("int", fileInfo.getDeftypesList().get(0).getTypeReference().getTypeName());
        Assert.assertEquals(true, fileInfo.getDeftypesList().get(0).getTypeReference().isPointer());

        Assert.assertEquals("year", fileInfo.getDeftypesList().get(1).getName());
        Assert.assertEquals("int", fileInfo.getDeftypesList().get(1).getTypeReference().getTypeName());
        Assert.assertEquals(false, fileInfo.getDeftypesList().get(1).getTypeReference().isPointer());

        //global vars
        Assert.assertEquals(2, fileInfo.getGlobalVariablesList().size());
        Assert.assertEquals("element", fileInfo.getGlobalVariablesList().get(0).getName());
        Assert.assertEquals("UtilsStructure", fileInfo.getGlobalVariablesList().get(0).getType().getTypeName());
        Assert.assertEquals("a", fileInfo.getGlobalVariablesList().get(1).getName());
        Assert.assertEquals("int", fileInfo.getGlobalVariablesList().get(1).getType().getTypeName());
        Assert.assertEquals(true, fileInfo.getGlobalVariablesList().get(1).getType().isPointer());
    }

    private ModuleInfo parseValue(String fileName, String packagePath) throws IOException {
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
