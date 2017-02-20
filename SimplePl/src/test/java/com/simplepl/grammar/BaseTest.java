package com.simplepl.grammar;

import com.simplepl.Const;
import com.simplepl.exception.ParseException;
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
            String value = loadFile(filename+Const.EXT);
            ParsingResult<Void> articleResult = runner.run(value);
            Assert.assertTrue(articleResult.matched);
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
}
