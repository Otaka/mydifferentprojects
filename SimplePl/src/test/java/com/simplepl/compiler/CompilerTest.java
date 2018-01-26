package com.simplepl.compiler;

import java.io.File;
import org.junit.Test;
import com.simplepl.Compiler;

public class CompilerTest {

    @Test
    public void testCompileModule() {
        Compiler compiler = new Compiler();
        compiler.getContext().getSrcRoot().addFileSystemRoot(new File(CompilerTest.class.getResource("testdata").getFile()));
        compiler.compileProgram("com.utils.arithmeticModule");
    }
}
