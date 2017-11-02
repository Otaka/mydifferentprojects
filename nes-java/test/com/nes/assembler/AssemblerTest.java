package com.nes.assembler;

import com.nes.NesAbstractTst;
import com.nes.processor.AluUtils;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class AssemblerTest extends NesAbstractTst {

    private Assembler assembler;

    public AssemblerTest() {
    }

    @Before
    public void setUp() {
        assembler = new Assembler();
    }

    @Test
    public void testAssembler() {
        byte[] bytes = assembler.assemble(new String[]{
                    "LDA #$c0  ;Load the hex value $c0 into the A register",
                    "TAX       ;Transfer the value in the A register to X",
                    "INX       ;Increment the value in the X register",
                    "ADC #$c4  ;Add the hex value $c4 to the A register",
                    "BRK       ;Break - we're done"
                });
        assertBytesEquals(new byte[]{(byte) 0xa9, (byte) 0xc0, (byte) 0xaa,
                    (byte) 0xe8, (byte) 0x69, (byte) 0xc4, (byte) 0x00},
                bytes);
    }

    @Test
    public void testAssemblerBranchBack() {
        byte[] bytes = assembler.assemble(new String[]{
                    "   LDX      #$08",
                    "decrement:",
                    "  DEX",
                    "  STX $0200",
                    "  CPX #$03",
                    "  BNE decrement",
                    "  STX $0201",
                    "  BRK"
                });
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{
                    (byte) 0xa2, 0x08, (byte) 0xca, (byte) 0x8e, 0x00, 0x02, (byte) 0xe0,
                    0x03, (byte) 0xd0, (byte) 0xf8, (byte) 0x8e, 0x01, 0x02, 0x00},
                bytes);
    }

    @Test
    public void testAssemblerBranchFront() {
        byte[] bytes = assembler.assemble(new String[]{
                    "  BNE decrement",
                    "  STX $0201",
                    "  decrement:",
                    "    BRK",});
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{(byte) 0xd0, 0x03, (byte) 0x8e, 0x01, 0x02, 0x00},
                bytes);
    }

    @Test
    public void testAssemblerJmpFront() {
        byte[] bytes = assembler.assemble(new String[]{
                    "  JMP decrement",
                    "brk",
                    "decrement:"
                });
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{0x4c, 0x04, 0x06, 0x00},
                bytes);
    }

    @Test
    public void testAssemblerJmpBack() {
        byte[] bytes = assembler.assemble(new String[]{
                    "decrement:",
                    "brk",
                    "  JMP decrement",});
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{0x00, 0x4c, 0x00, 0x06},
                bytes);
    }

    @Test
    public void testAssemblerJmpIndirect() {
        byte[] bytes = assembler.assemble(new String[]{
                    "JMP ($00f0)"
                });
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{(byte) 0x6c, (byte) 0xf0, 0x00},
                bytes);
    }
    
    @Test
    public void testAssemblerJmpOnAddress() {
        byte[] bytes = assembler.assemble(new String[]{
                    "JMP $00f0"
                });
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{(byte) 0x4c, (byte) 0xf0, 0x00},
                bytes);
    }

    @Test
    public void testAssembler_STA_ZP() {
        byte[] bytes = assembler.assemble(new String[]{
                    "sta $03",});
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(new byte[]{(byte) 0x85, 0x03},
                bytes);
    }

    @Test
    public void testAssemblerBigTest() {
        String source = readString("bigtest.txt");
        byte[] result = readByteArrayFromHexTextFile("bigtestresult.txt");
        byte[] bytes = assembler.assemble(source);
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(result,
                bytes);
    }

    @Test
    public void testAssemblerJsrFront() {
        String source = readString("jsrFront.txt");
        byte[] result = readByteArrayFromHexTextFile("jsrFrontResult.txt");
        byte[] bytes = assembler.assemble(source);
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(result,
                bytes);
    }

    @Test
    public void testAssemblerJsrBack() {
        String source = readString("jsrBack.txt");
        byte[] result = readByteArrayFromHexTextFile("jsrBackResult.txt");
        byte[] bytes = assembler.assemble(source);
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(result,
                bytes);
    }

    @Test
    public void testAssemblerAllCommandsTestResult() {
        String source = readString("allCommandsTest.txt");
        byte[] result = readByteArrayFromHexTextFile("allCommandsTestResult.txt");
        byte[] bytes = assembler.assemble(source);
        AluUtils.bytesToHexString(bytes);
        assertBytesEquals(result,
                bytes);
    }
}
