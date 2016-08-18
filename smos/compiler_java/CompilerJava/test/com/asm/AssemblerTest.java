package com.asm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author sad
 */
public class AssemblerTest {

    public AssemblerTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testSplitCommand() {
        SplittedCommand command = Assembler.parseAssembleLine("cld");
        Assert.assertEquals("cld", command.getCommand());
        Assert.assertEquals(0,command.getArgs().length);

        command = Assembler.parseAssembleLine("mov ax, bx");
        Assert.assertEquals("mov", command.getCommand());
        Assert.assertEquals("ax", command.getArgs()[0]);
        Assert.assertEquals("bx", command.getArgs()[1]);
        
        command = Assembler.parseAssembleLine("pop ax");
        Assert.assertEquals("pop", command.getCommand());
        Assert.assertEquals("ax", command.getArgs()[0]);
        
        command = Assembler.parseAssembleLine("mov ax , [bp] ");
        Assert.assertEquals("mov", command.getCommand());
        Assert.assertEquals("ax", command.getArgs()[0]);
        Assert.assertEquals("[bp]", command.getArgs()[1]);

    }

}
