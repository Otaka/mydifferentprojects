package com.kotorresearch.script;

import com.kotorresearch.script.interpreter.ScriptEvaluator;
import com.kotorresearch.script.interpreter.FunctionsManager;
import com.kotorresearch.script.assembler.NcsAssembler;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ScriptEvaluatorTest {

    public ScriptEvaluatorTest() {
    }

    @Test
    public void testCommands() {
        FunctionsManager functionsManager = new FunctionsManager();
        MockFunctions mockFunctions = new MockFunctions();
        functionsManager.addFunctionsHolderObject(mockFunctions);
        NcsAssembler assembler = new NcsAssembler();
        byte[] buffer = assembler.assemble("CONSTS \"testString\"\nACTION PrintString,1\nRETN", true);
        ScriptEvaluator scriptEvaluator = new ScriptEvaluator(buffer, "testScript", functionsManager,false);
        scriptEvaluator.evaluate();
        Assert.assertEquals(1, mockFunctions.getPrintedStrings().size());
        Assert.assertEquals("testString", mockFunctions.getPrintedStrings().get(0));
        
    }
}
