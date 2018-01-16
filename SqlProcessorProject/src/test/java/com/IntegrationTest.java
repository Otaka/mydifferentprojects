package com;

import net.sf.jsqlparser.JSQLParserException;
import org.junit.Test;

/**
 * @author sad
 */
public class IntegrationTest extends SqlExecutionTestRunner {

    @Test
    public void testIntegration() throws JSQLParserException, Exception {
        checkTestFilesFromResourceFolder("/com/resources/");
    }
}
