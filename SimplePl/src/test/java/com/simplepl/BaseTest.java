package com.simplepl;

import com.google.gson.GsonBuilder;
import com.simplepl.ast.AstTestTest;
import com.simplepl.exception.ParseException;
import com.simplepl.grammar.MainParser;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.grammar.ast.AstPrinter;
import com.simplepl.grammar.comments.CommentRemover;
import com.simplepl.utils.AstCollection;
import com.simplepl.utils.AstDeserializer;
import com.simplepl.utils.AstMatcher;
import com.simplepl.utils.AstMatcherParser;
import com.simplepl.utils.AstValue;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Assert;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author Dmitry
 */
public class BaseTest {

    public MainParser createParser() {
        MainParser parser = Parboiled.createParser(MainParser.class);
        // MainParser.setEnableAction(false);
        return parser;
    }

    protected String loadFile(String fileName) throws IOException {
        String fullPath = "/com/simplepl/grammar/testdata/" + fileName;
        InputStream stream = BaseTest.class.getResourceAsStream(fullPath);
        if (stream == null) {
            throw new IllegalArgumentException("Cannot find file " + fullPath);
        }

        String value = IOUtils.toString(new BOMInputStream(stream));
        return value;
    }

    public void checkFileRuleSuccess(MainParser parser, Rule rule, String filename) throws IOException {
        try {
            ParseRunner runner = new BasicParseRunner(rule);
            String text = loadFile(filename + Const.EXT);
            CommentRemover commentRemover = new CommentRemover(text);
            String cleanedFromComments = commentRemover.process();
            ParsingResult<Object> articleResult = runner.run(cleanedFromComments);
            Assert.assertTrue(articleResult.matched);
            /* if (!articleResult.valueStack.isEmpty()) {
                AstPrinter printer = new AstPrinter();
                //printer.printAstTree((Ast)articleResult.valueStack.pop());
                int x = 0;
                x++;
            }*/
        } catch (ParserRuntimeException ex) {
            if (ex.getCause() instanceof ParseException) {
                throw (ParseException) ex.getCause();
            }
            throw ex;
        }
    }

    public void checkTextRuleSuccess(MainParser parser, Rule rule, String text) {
        try {
            ParseRunner runner = new BasicParseRunner(rule);
            ParsingResult<Void> articleResult = runner.run(text);
            Assert.assertTrue(articleResult.matched);
        } catch (ParserRuntimeException ex) {
            if (ex.getCause() instanceof ParseException) {
                throw (ParseException) ex.getCause();
            }
            throw ex;
        }
    }

    public void checkTextRuleFailure(MainParser parser, Rule rule, String text) {
        ParseRunner runner = new BasicParseRunner(rule);
        try {
            ParsingResult<Void> articleResult = runner.run(text);
            Assert.assertFalse(articleResult.matched);
        } catch (ParserRuntimeException ex) {
            if (ex.getCause() instanceof ParseException) {
                //good. Test is passed
                return;
            }
            throw ex;
        }
    }

    public void testAst(String expressionToTest, String jsonAstMatcher) {
        MainParser parser = createParser();
        ParseRunner runner = new BasicParseRunner(parser.testExpression());
        ParsingResult<Object> articleResult = runner.run(expressionToTest);
        Assert.assertTrue(articleResult.matched);
        Ast ast = (Ast) articleResult.valueStack.pop();
        AstMatcher matcher = new AstMatcherParser().parse(jsonAstMatcher);
        matcher.match(ast);

    }

    public void testAstExpressionFromFile(String expressionToTest, String file, String jsonKey) {
        MainParser parser = createParser();
        testAstRuleFromFile(expressionToTest, file, jsonKey, parser.testExpression());
    }

    private void printAst(Ast ast){
        new AstPrinter().printAstTree(ast);
    }
    
    public void testAstRuleFromFile(String expressionToTest, String file, String jsonKey, Rule rule) {
        ParseRunner runner = new BasicParseRunner(rule);
        ParsingResult<Object> articleResult = runner.run(expressionToTest);
        Assert.assertTrue(articleResult.matched);
        Ast ast = (Ast) articleResult.valueStack.pop();
        String filePath = "/com/simplepl/ast/testdata/" + file + ".json";
        InputStream stream = AstTestTest.class.getResourceAsStream(filePath);
        if (stream == null) {
            System.out.println("Cannot find file [" + filePath + "]");
            System.out.print("\""+jsonKey+"\": ");printAst(ast);
            throw new IllegalArgumentException("Cannot find file [" + filePath + "]");
        }
        String jsonText;
        try {
            jsonText = IOUtils.toString(stream, "UTF-8");
        } catch (IOException ex) {
            System.out.println("Cannot read file [" + filePath + "]");
            System.out.print("\""+jsonKey+"\": ");printAst(ast);
            throw new RuntimeException(ex);
        }
//printAst(ast);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AstValue.class, new AstDeserializer());
        AstCollection collection = gsonBuilder.create().fromJson(jsonText, AstCollection.class);
        AstMatcher matcher = collection.getCollection().get(jsonKey);
        if (matcher == null) {
            System.out.println("Cannot find [" + jsonKey + "] in  " + file);
            System.out.print("\""+jsonKey+"\": ");printAst(ast);
            throw new IllegalArgumentException("Cannot find [" + jsonKey + "] in  " + file);
        }

        try{
            matcher.match(ast);
        }catch(IllegalArgumentException ex){
            System.out.print("\""+jsonKey+"\": ");printAst(ast);
            throw ex;
        }
    }
}
