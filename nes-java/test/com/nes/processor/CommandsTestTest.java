package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class CommandsTestTest extends NesAbstractTst {

    @Test
    public void testAlu() {
        int[] b = new int[]{0xa2, 0x01, 0xa9, 0xaa, 0x95, 0xa0, 0xe8, 0x95, 0xa0};
        ALU alu = testAlu(b, 0xaa, 0x02, 0, 0xfd, 0x60a, false, false, false, false);
        assertByteEquals((byte) 0xaa, alu.getMemory().getValue(0xa1));
        assertByteEquals((byte) 0xaa, alu.getMemory().getValue(0xa2));

        b = new int[]{0xa9, 0x01, 0x85, 0xf0, 0xa9, 0xcc, 0x85, 0xf1, 0x6c, 0xf0, 0x00};
        testAlu(b, 0xcc, 0x00, 0x00, 0xfd, 0xcc02, false, false, true, false);
    }

    @Test
    public void testAdc() {
        int[] b = new int[]{0xa9, 0x05, 0x69, 0x08};//LDA #$5; ADC #$8
        ALU alu = testAlu(b, 0x0d, 0x00, 0, 0xfd, 0x605, false, false, false, false);
    }

    @Test
    public void testAdcC() {
        int[] b = new int[]{0x38, 0xa9, 0x05, 0x69, 0x08};//SEC, LDA #$5; ADC #$8
        testAlu(b, 0x0e, 0x00, 0, 0xfd, 0x606, false, false, false, false);
        b = new int[]{0x38, 0xa9, 0x05, 0x69, 0xff};//SEC, LDA #$5; ADC #$FF
        testAlu(b, 0x05, 0x00, 0, 0xfd, 0x606, true, false, false, false);
    }

    @Test
    public void testAdd_Z_X() {
        int[] b = new int[]{0xa9, 0x05, 0xa2, 0x03, 0x95, 0x06, 0xa9, 0x10, 0x75, 0x06};//LDA #$5; LDX #$3; STA $6,X;LDA #$10; ADC $6,X
        ALU alu = testAlu(b, 0x15, 0x03, 0x0, 0xfd, 0x60b, false, false, false, false);
        assertByteEquals((byte) 0x5, alu.getMemory().getValue(9));
    }

    @Test
    public void testAdd_Absolute() {
        int[] b = new int[]{0xa9, 0x20, 0x8d, 0x00, 0xc0, 0xa9, 0x05, 0x6d, 0x00, 0xc0};//LDA #$20; STA $c000; LDA #$5; ADC $c000
        ALU alu = testAlu(b, 0x25, 0x00, 0x0, 0xfd, 0x60b, false, false, false, false);
        assertByteEquals((byte) 0x20, alu.getMemory().getValue(0xc000));
    }

    @Test
    public void testAdd_Absolute_X() {
        //LDX #$5;LDA #$20;STA $a000,X;LDA #$5;ADC $a000,X
        int[] b = new int[]{0xa2, 0x05, 0xa9, 0x20, 0x9d, 0x00, 0xa0, 0xa9, 0x05, 0x7d, 0x00, 0xa0};
        ALU alu = testAlu(b, 0x25, 0x05, 0x0, 0xfd, 0x60d, false, false, false, false);
        assertByteEquals((byte) 0x20, alu.getMemory().getValue(0xa005));
    }

    @Test
    public void testAdd_Absolute_Y() {
        //LDY #$5; LDA #$20; STA $a000,Y; LDA #$5; ADC $a000,Y
        int[] b = new int[]{0xa0, 0x05, 0xa9, 0x20, 0x99, 0x00, 0xa0, 0xa9, 0x05, 0x79, 0x00, 0xa0};
        ALU alu = testAlu(b, 0x25, 0x00, 0x5, 0xfd, 0x60d, false, false, false, false);
        assertByteEquals((byte) 0x20, alu.getMemory().getValue(0xa005));
    }

    @Test
    public void testAdd_Indirect_X() {
        //LDX #$01; LDA #$05; STA $01; LDA #$06; STA $02; LDY #$0a; STY $0605; LDA ($00,X)
        int[] b = new int[]{0xa2, 0x01, 0xa9, 0x05, 0x85, 0x01, 0xa9, 0x06, 0x85, 0x02, 0xa0, 0x0a, 0x8c, 0x05, 0x06, 0xa1, 0x00};
        testAlu(b, 0xa, 0x01, 0x0a, 0xfd, 0x612, false, false, false, false);
        //assertByteEquals((byte) 0x20, alu.getMemory().getValue(0xa005));
    }

    @Test
    public void testAdd_Indirect_Y() {
        //LDY #$01;LDA #$03;STA $01;LDA #$07;STA $02;LDX #$0a;STX $0704;LDA ($01),Y
        int[] b = new int[]{0xa0, 0x01, 0xa9, 0x03, 0x85, 0x01, 0xa9, 0x07, 0x85, 0x02, 0xa2, 0x0a, 0x8e, 0x04, 0x07, 0xb1, 0x01};
        ALU alu = testAlu(b, 0xa, 0xa, 0x01, 0xfd, 0x612, false, false, false, false);
        assertByteEquals((byte) 0xa, alu.getMemory().getValue(0x0704));
    }

    @Test
    public void testPush() {
        //  LDA #$20;PHA;LDA #$0;PLA
        int[] b = new int[]{0xa9, 0x20, 0x48, 0xa9, 0x00, 0x68};
        testAlu(b, 0x20, 0x0, 0x0, 0xfd, 0x607, false, false, false, false);
    }

    @Test
    public void testStack() {
        //  LDX #$00;  LDY #$00;firstloop:;  TXA;  STA $0200,Y;  PHA;  INX;  INY;  CPY #$10;  BNE firstloop ;loop until Y is $10;secondloop:;  PLA;  STA $0200,Y;  INY;  CPY #$20      ;loop until Y is $20;  BNE secondloop
        int[] b = new int[]{0xa2, 0x00, 0xa0, 0x00, 0x8a, 0x99, 0x00, 0x02, 0x48, 0xe8, 0xc8, 0xc0, 0x10, 0xd0, 0xf5, 0x68, 0x99, 0x00, 0x02, 0xc8, 0xc0, 0x20, 0xd0, 0xf7};
        ALU alu = testAlu(b, 0x0, 0x10, 0x20, 0xfd, 0x619, true, true, false, false);
        assertByteEquals((byte) 0x0, alu.getMemory().getValue(0x200));
        assertByteEquals((byte) 0x1, alu.getMemory().getValue(0x201));
        assertByteEquals((byte) 0x2, alu.getMemory().getValue(0x202));
        assertByteEquals((byte) 0x3, alu.getMemory().getValue(0x203));
        assertByteEquals((byte) 0x4, alu.getMemory().getValue(0x204));
        assertByteEquals((byte) 0x5, alu.getMemory().getValue(0x205));
        assertByteEquals((byte) 0x6, alu.getMemory().getValue(0x206));
        assertByteEquals((byte) 0x7, alu.getMemory().getValue(0x207));
        assertByteEquals((byte) 0x8, alu.getMemory().getValue(0x208));
        assertByteEquals((byte) 0x9, alu.getMemory().getValue(0x209));
        assertByteEquals((byte) 0xa, alu.getMemory().getValue(0x20a));
        assertByteEquals((byte) 0xb, alu.getMemory().getValue(0x20b));
        assertByteEquals((byte) 0xc, alu.getMemory().getValue(0x20c));
        assertByteEquals((byte) 0xd, alu.getMemory().getValue(0x20d));
        assertByteEquals((byte) 0xe, alu.getMemory().getValue(0x20e));
        assertByteEquals((byte) 0xf, alu.getMemory().getValue(0x20f));
        assertByteEquals((byte) 0xf, alu.getMemory().getValue(0x210));
        assertByteEquals((byte) 0xe, alu.getMemory().getValue(0x211));
        assertByteEquals((byte) 0xd, alu.getMemory().getValue(0x212));
        assertByteEquals((byte) 0xc, alu.getMemory().getValue(0x213));
        assertByteEquals((byte) 0xb, alu.getMemory().getValue(0x214));
        assertByteEquals((byte) 0xa, alu.getMemory().getValue(0x215));
        assertByteEquals((byte) 0x9, alu.getMemory().getValue(0x216));
        assertByteEquals((byte) 0x8, alu.getMemory().getValue(0x217));
        assertByteEquals((byte) 0x7, alu.getMemory().getValue(0x218));
        assertByteEquals((byte) 0x6, alu.getMemory().getValue(0x219));
        assertByteEquals((byte) 0x5, alu.getMemory().getValue(0x21a));
        assertByteEquals((byte) 0x4, alu.getMemory().getValue(0x21b));
        assertByteEquals((byte) 0x3, alu.getMemory().getValue(0x21c));
        assertByteEquals((byte) 0x2, alu.getMemory().getValue(0x21d));
        assertByteEquals((byte) 0x1, alu.getMemory().getValue(0x21e));
        assertByteEquals((byte) 0x0, alu.getMemory().getValue(0x21f));
        assertByteEquals((byte) 0x0, alu.getMemory().getValue(0x220));
    }

    @Test
    public void testJmpAbs() {
        //nop;nop;jmp label;nop;nop;sed;sec;label:brk
        int[] b = new int[]{0xea, 0xea, 0x4c, 0x09, 0x06, 0xea, 0xea, 0xf8, 0x38, 0x00};
        testAlu(b, 0x00, 0x00, 0, 0xfd, 0x60a, false, false, false, false);
    }
}
