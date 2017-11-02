/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nes.processor;

import com.nes.NesAbstractTst;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ALUTest extends NesAbstractTst {

    public ALUTest() {
    }

    @Before
    public void setUp() {
    }
    
    @Test
    public void testHexStringToByteArray(){
        org.junit.Assert.assertArrayEquals(new byte[]{(byte)0x10,(byte)0xba},AluUtils.hexStringToByteArray("10 ba"));
    }

    @Test
    public void testAddBinary() {
        checkAddBinary((byte) 0x6, (byte) 0xf9, (byte) 0xff, true, false, false);
        checkAddBinary((byte) 0x7, (byte) 0xfe, (byte) 0x5, false, false, true);
        checkAddBinary((byte) 0x7, (byte) 0x2, (byte) 0x9, false, false, false);
        checkAddBinary((byte) 0x7, (byte) 0x80, (byte) 0x87, true, false, false);
        checkAddBinary((byte) 0x7, (byte) -9, (byte) -2, true, false, false);
        checkAddBinary((byte) 0x7, (byte) 0x7a, (byte) 0x81, true, true, false);
        checkAddBinary((byte) 0x80, (byte) 0x90, (byte) 0x10, false, true, true);
        checkAddBinary((byte) 0xf0, (byte) 0xf0, (byte) 0xe0, true, false, true);
        checkAddBinary((byte) 0xf8, (byte) 0x0a, (byte) 2, false, false, true);
        checkAddBinary((byte) 0x60, (byte) 0xab, (byte) 0xb, false, false, true);
        checkAdCbinary((byte) 0x6, (byte) 0xf9, (byte) 0x0, false, false, true);
        checkAdCbinary((byte) 0x6, (byte) 0xf8, (byte) 0xff, true, false, false);
        checkAdCbinary((byte) 0x80, (byte) 0x90, (byte) 0x11, false, true, true);
        checkAdCbinary((byte) 0x05, (byte) 0xff, (byte) 0x05, false, false, true);
    }

    @Test
    public void testAddBcd() {
        checkAddBcdCarry((byte) 0x1, (byte) 0x69, (byte) 0x6b, false, false, false);
        checkAddBcd((byte) 0x6, (byte) 0x2, (byte) 0x8, false, false, false);
       // checkAddBcd((byte) 0x6, (byte) 0x8, (byte) 0x14, false, false, false);
       // checkAddBcd((byte) 0x25, (byte) 0x38, (byte) 0x63, false, false, false);
       // checkAddBcd((byte) 0x48, (byte) 0x51, (byte) 0x99, false, false, false);
       // checkAddBcd((byte) 0x10, (byte) 0x1a, (byte) 0x30, false, false, false);
       // checkAddBcd((byte) 0xb4, (byte) 0x1a, (byte) 0x34, false, false, true);
       // checkAddBcd((byte) 0x10, (byte) 0x39, (byte) 0x49, false, false, false);
       // checkAddBcd((byte) 0x1b, (byte) 0x2f, (byte) 0x40, false, false, false);
        
    }

    private void checkAddBcd(byte a, byte b, byte result, boolean negative, boolean flagOverflow, boolean flagCarry) {
        ALU alu = new ALU();
        alu.getAluFlags().setDecimal(true);
        byte actualResult = alu.add(a, b);
        assertByteEquals(result, actualResult);
        Assert.assertEquals(flagOverflow, alu.getAluFlags().isOverflow());
        Assert.assertEquals(negative, alu.getAluFlags().isNegative());
        Assert.assertEquals(flagCarry, alu.getAluFlags().isCarry());
    }
    
    private void checkAddBcdCarry(byte a, byte b, byte result, boolean negative, boolean flagOverflow, boolean flagCarry) {
        ALU alu = new ALU();
        alu.getAluFlags().setDecimal(true);
        alu.getAluFlags().setCarry(true);
        
        byte actualResult = alu.add(a, b);
        assertByteEquals(result, actualResult);
        Assert.assertEquals(flagOverflow, alu.getAluFlags().isOverflow());
        Assert.assertEquals(negative, alu.getAluFlags().isNegative());
        Assert.assertEquals(flagCarry, alu.getAluFlags().isCarry());
    }

    private void checkAddBinary(byte a, byte b, byte result, boolean negative, boolean flagOverflow, boolean flagCarry) {
        ALU alu = new ALU();
        byte actualResult = alu.add(a, b);
        assertByteEquals(result, actualResult);
        Assert.assertEquals(flagOverflow, alu.getAluFlags().isOverflow());
        Assert.assertEquals(negative, alu.getAluFlags().isNegative());
        Assert.assertEquals(flagCarry, alu.getAluFlags().isCarry());
    }

    private void checkAdCbinary(byte a, byte b, byte result, boolean negative, boolean flagOverflow, boolean flagCarry) {
        ALU alu = new ALU();
        alu.getAluFlags().setCarry(true);
        byte actualResult = alu.add(a, b);
        assertByteEquals(result, actualResult);
        Assert.assertEquals(flagOverflow, alu.getAluFlags().isOverflow());
        Assert.assertEquals(negative, alu.getAluFlags().isNegative());
        Assert.assertEquals(flagCarry, alu.getAluFlags().isCarry());
    }
}
