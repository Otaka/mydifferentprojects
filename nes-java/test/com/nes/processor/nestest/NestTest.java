package com.nes.processor.nestest;

import com.nes.NesAbstractTst;
import com.nes.processor.ALU;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import nesemulator.NesEmulator;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class NestTest extends NesAbstractTst {

    @Test
    public void bigNesProcessorTest() throws IOException {
        String[] lines = readLines();
        testAlu(readCode(), lines);
    }

    protected static void testAlu(byte[] b, String[] lines) throws IOException {
        ALU alu = new ALU();
        alu.getMemory().setValue((byte) 0xA9, 0xA9A9);
        alu.getMemory().setValue((byte) 0xFF, 0x4015);
        alu.getMemory().setValue((byte) 0xff, 0x4004);
        alu.getMemory().setValue((byte) 0xff, 0x4005);
        alu.getMemory().setValue((byte) 0xff, 0x4006);
        alu.getMemory().setValue((byte) 0xFF, 0x4007);
        try {
            alu.getMemory().setBytes(b, 0xc000);
        } catch (Exception ex) {
            //ignore
        }

        alu.setPc(0xC000);
        for (int i = 0; i < 8991; i++) {
            String decoded = alu.decodeCommand(true);
            Assert.assertEquals("Error: line №" + (i + 1), lines[i],decoded);
            alu.executeCommand();
        }
    }

     private static byte[] readCode() {
        byte[] actualBytes;
        try {
            try (InputStream stream = NesEmulator.class.getResourceAsStream("/com/nes/testdata/nestest.nes")) {
                byte[] bytes = IOUtils.toByteArray(stream);
                actualBytes = Arrays.copyOfRange(bytes, 16, bytes.length);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return actualBytes;
    }
    
    private static String[] readLines() {
        String[] lines;
        try {
            try (InputStream stream = NesAbstractTst.class.getResourceAsStream("/com/nes/testdata/nestest.log")) {
                lines = IOUtils.readLines(stream).toArray(new String[0]);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return lines;
    }
}
