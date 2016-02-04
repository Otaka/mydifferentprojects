package com.macro;

import com.LikeFunction;
import com.macro.exception.MacrosRuntimeException;
import com.macro.functions.AbstrMacroFunction;
import com.macro.functions.AbstrMacroFunctionWithoutBrackets;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author sad
 */
public class MacroProcessorTest {

    public MacroProcessorTest() {
    }

    private class PrintFunction extends AbstrMacroFunction {

        @Override
        public String process(List<String> args) {
            if (args.isEmpty()) {
                throw new MacrosRuntimeException("Macro function print should not have empty argument list");
            }

            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append("println(").append(arg).append(");");
            }

            return sb.toString();
        }
    }

    private class ToLowerFunction extends AbstrMacroFunction {

        @Override
        public String process(List<String> args) {
            if (args.size() != 1) {
                throw new MacrosRuntimeException("Macro function ToLower should have 1 string argument");
            }

            return args.get(0).toLowerCase();
        }
    }

    private class ToLowerFunctionWithoutBrackets extends AbstrMacroFunctionWithoutBrackets {

        @Override
        public String process(List<String> args) {
            return args.get(0).toLowerCase();
        }
    }

    @Test
    public void testProcessWithoutMacro() {
        MacroProcessor processor = new MacroProcessor();
        String input = "mytext;\n\tKsler 454.345 (^ 343\n//asdasd\n/*asdasdas\n*/ sadkjh";
        String result = processor.process(input);
        assertEquals(input, result);
    }

    @Test
    public void testProcess() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("PRINT", new PrintFunction());
        String result = processor.process("mytext;\nPRINT('someValue')\n");
        assertEquals("mytext;\nprintln('someValue');\n", result);
    }

    @Test
    public void testMacroWithoutBrackets() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("L", new ToLowerFunctionWithoutBrackets());
        String result = processor.process("L'MyString'Ad L\"AbCd\"");
        assertEquals("'mystring'Ad \"abcd\"", result);
    }

    @Test
    public void testMacroInMacroArgument() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("PRINT", new PrintFunction());
        processor.addMacroFunction("LOW", new ToLowerFunction());
        String result = processor.process("'a';PRINT('A',LOW('B'),'C')");
        assertEquals("'a';println('A');println('b');println('C');", result);
    }

    @Test
    public void testLIKE() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("LIKE", new LikeFunction());
        processor.addMacroFunction("L", new ToLowerFunctionWithoutBrackets());

        String result = processor.process("LIKE(c,L'%W%', '%1')");
        assertEquals("c.contains('w')||c.endsWith('1')", result);

        result = processor.process("LIKE(c,'w')");
        assertEquals("c=='w'", result);

        result = processor.process("LIKE(c,'%w')");
        assertEquals("c.endsWith('w')", result);

        result = processor.process("LIKE(c,'w%')");
        assertEquals("c.startsWith('w')", result);

        result = processor.process("LIKE(c,'%w%')");
        assertEquals("c.contains('w')", result);

        result = processor.process("LIKE(c,'%1%2%')");
        assertEquals("like2Parts(c,'1','2',false,false)", result);

        result = processor.process("LIKE(c,'1%2%')");
        assertEquals("like2Parts(c,'1','2',true,false)", result);

        result = processor.process("LIKE(c,'1%2')");
        assertEquals("like2Parts(c,'1','2',true,true)", result);

        result = processor.process("LIKE(c,'%1%2%34')");
        assertEquals("like3Parts(c,'1','2','34',false,true)", result);

        result = processor.process("LIKE(c,'%1%2%34%67')");
        assertEquals("likeNParts(c,false,true,'1','2','34','67')", result);
    }

    @Test
    public void testLIKE_RegexpCases() {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("LIKE", new LikeFunction());
        String result = processor.process("LIKE(c,'%ge-#/#/#%')");
        assertEquals("likeRegexp(c,'ge-\\\\d+/\\\\d+/\\\\d+')", result);

        result = processor.process("LIKE(c,'ge#%')");
        assertEquals("likeRegexp(c,'^ge\\\\d+')", result);

        result = processor.process("LIKE(c,'ge#%')");
        assertEquals("likeRegexp(c,'^ge\\\\d+')", result);
    }

    @Test
    public void testMacroNotBreakTheLineNumbers() throws IOException {
        MacroProcessor processor = new MacroProcessor();
        processor.addMacroFunction("LIKE", new LikeFunction());
        String value = IOUtils.toString(MacroProcessorTest.class.getResourceAsStream("testFile.html"));
        assertNotNull("File 'testFile.html' is not found in package with test class", value);
        String result = processor.process(value);
        String lines[] = result.split("\\r?\\n");
        assertEquals(4, lines.length);
        assertTrue(lines[2].contains("this line should be on 3 row after expanding"));
    }
}
