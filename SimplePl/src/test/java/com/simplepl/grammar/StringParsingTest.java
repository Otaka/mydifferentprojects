package com.simplepl.grammar;

import com.simplepl.BaseTest;
import java.io.IOException;
import org.junit.Test;

public class StringParsingTest extends BaseTest {

    @Test
    public void testGenericString() throws IOException {
        MainParser parser = createParser();
        checkTextRuleSuccess(parser, parser.testGenericStringRule(), "\"mystring\\nnewline\"");
        checkTextRuleFailure(parser, parser.testGenericStringRule(), "\"mystring\\nnewline");
    }
    
    @Test
    public void testRawString() throws IOException {
        MainParser parser = createParser();
        checkTextRuleSuccess(parser, parser.testRawStringRule(), "\"\"\"mystring\"\"\"");
        checkTextRuleFailure(parser, parser.testRawStringRule(), "\"\"\"mystringnewline");
    }
}
