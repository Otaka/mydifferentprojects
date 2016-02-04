package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class AllCommandsTest extends NesAbstractTst {

    @Test
    public void testAllCommands() {
        System.out.println("ignored test AllCommandsTest");
        /*String text = readString("commandsTest.txt");
         byte[] expectedByteCode = readByteArrayFromHexTextFile("commandsByteCodeTestResult.txt");
         Assembler assembler = new Assembler();
         byte[] bytecode = assembler.assemble(text);
         assertBytesEquals(expectedByteCode, bytecode);
         ALU alu = testAlu(bytecode, 0x44, 0x043, 0x44, 0xfd, 0x822, true, false, false, false);

         byte[] memoryExpectResult = readByteArrayFromHexTextFile("commandsTestMemoryResult.txt");
         byte[] memoryActual = new byte[memoryExpectResult.length];
         for (int i = 0; i < memoryExpectResult.length; i++) {
         memoryActual[i] = alu.getMemory().getValue(0x1000 + i);
         }
         assertBytesEquals(memoryExpectResult, memoryActual);*/
    }
}
