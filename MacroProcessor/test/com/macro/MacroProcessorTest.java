package com.macro;

import com.macro.functions.AbstrMacroFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author sad
 */
public class MacroProcessorTest {

    public MacroProcessorTest() {
    }

    private class PrintFunction extends AbstrMacroFunction {

    }

    @Test
    public void testProcessWithoutMacro() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("PRINT", new PrintFunction());
        String input = "mytext;\n\tKsler 454.345 (^ 343\n//asdasd\n/*asdasdas\n*/ sadkjh";
        String result = processor.process(input);
        assertEquals(input, result);
    }

    @Test
    public void testProcess() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("PRINT", new PrintFunction());
        processor.process("mytext;\nPRINT('someValue')\n");
    }
}
