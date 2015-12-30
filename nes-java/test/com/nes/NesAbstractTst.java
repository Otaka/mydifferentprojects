package com.nes;

import com.nes.assembler.Assembler;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

/**
 * @author sad
 */
public abstract class NesAbstractTst {

    protected byte[] readByteArrayFromHexTextFile(String file) {
        String str = readString(file);
        return AluUtils.hexStringToByteArray(str);
    }

    protected static void assertByteEquals(byte expected, byte actual) {
        if (expected != actual) {
            Assert.fail("Failed test. Expected " + expected + ". Found " + actual);
        }
    }

    protected static void assertBytesEquals(byte[] expected, byte[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if ((expected != null && actual == null) || (expected == null && actual != null)) {
            Assert.fail("Expected " + expected == null ? "null" : "not null" + " but found " + actual == null ? "null" : "not null");
        }
        if (expected.length != actual.length) {
            Assert.fail("Expected size of array =" + expected.length + " but actual=" + actual.length);
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                Assert.fail("Failed test. Expected [" + i + "]=" + expected[i] + ". Found [" + i + "]=" + actual[i]);
            }
        }
    }

    protected ALU testAlu(byte[] b, int a, int x, int y, int s, int p, boolean c, boolean z, boolean n, boolean o) {
        int[] intArray = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            intArray[i] = b[i] & 0xFF;
        }
        return testAlu(intArray, a, x, y, s, p, c, z, n, o);
    }

    protected ALU testAlu(String[] lines, int a, int x, int y, int s, int p, boolean c, boolean z, boolean n, boolean o) {
        Assembler assembler = new Assembler();
        byte[] bytes = assembler.assemble(lines);
        return testAlu(bytes, a, x, y, s, p, c, z, n, o);
    }

    protected ALU testAlu(int[] b, int a, int x, int y, int s, int p, boolean c, boolean z, boolean n, boolean o) {
        //   FileWriter writer = null;
        // try {
        //  writer = new FileWriter("D:\\log.txt", true);
        ALU alu = new ALU();
        alu.getMemory().setBytes(b, 0x600);
        alu.setPc(0x600);
        for (int i = 0; !alu.getAluFlags().isBreakFlag(); i++) {
            // String decoded = alu.decodeCommand(true);
            // writer.append(decoded);
            // writer.append("\n");
            // writer.flush();
            alu.executeCommand();
        }
        assertByteEquals((byte) a, alu.getA());
        assertByteEquals((byte) x, alu.getX());
        assertByteEquals((byte) y, alu.getY());
        assertByteEquals((byte) s, (byte) alu.getS());
        if (p != alu.getPc()) {
            junit.framework.Assert.fail("PC is not good. Expected " + Integer.toHexString(p) + ". Found " + Integer.toHexString(alu.getPc()));
        }
        if (c != alu.getAluFlags().isCarry()) {
            junit.framework.Assert.fail("Carry is not good");
        }
        if (z != alu.getAluFlags().isZero()) {
            junit.framework.Assert.fail("Zero flag is not good");
        }
        if (n != alu.getAluFlags().isNegative()) {
            junit.framework.Assert.fail("Negative flag is not good");
        }
        if (o != alu.getAluFlags().isOverflow()) {
            junit.framework.Assert.fail("Overflow flag is not good");
        }
        return alu;
        // } catch (IOException ex) {
        //     throw new RuntimeException(ex);
        // } finally {
        //     try {
        //         writer.close();
        //     } catch (IOException ex) {
        //         Logger.getLogger(NesAbstractTst.class.getName()).log(Level.SEVERE, null, ex);
        //     }
        // }
    }

    protected String readString(String fileName) {
        InputStream stream = NesAbstractTst.class.getResourceAsStream("/com/nes/testdata/" + fileName);
        if (stream == null) {
            throw new RuntimeException("Cannot found testFile " + fileName);
        }
        try {
            return IOUtils.toString(stream, "UTF8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected byte[] readByteArray(String fileName) throws IOException {
        File file = new File(fileName);
        int length = (int) file.length();
        byte[] array = new byte[length];
        InputStream stream = NesAbstractTst.class.getResourceAsStream("/com/nes/testdata/" + fileName);
        stream.read(array);
        stream.close();
        return array;
    }
}
