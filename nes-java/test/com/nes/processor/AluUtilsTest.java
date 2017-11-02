/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sad
 */
public class AluUtilsTest extends NesAbstractTst {

    public AluUtilsTest() {
    }

    @Test
    public void testSetBit() {
        assertByteEquals((byte) 0, AluUtils.setBit((byte) 1, 0, false));
        assertByteEquals((byte) 37, AluUtils.setBit((byte) 5, 5, true));
        try {
            assertByteEquals((byte) 37, AluUtils.setBit((byte) 5, 8, true));
            Assert.fail("Failed. Should throw exception");
        } catch (IllegalArgumentException ex) {
        }
        try {
            assertByteEquals((byte) 37, AluUtils.setBit((byte) 5, -8, true));
            Assert.fail("Failed. Should throw exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testIsBit() {
        Assert.assertEquals(true, AluUtils.isBit((byte) 5, 2));
        Assert.assertEquals(false, AluUtils.isBit((byte) 5, 3));
        Assert.assertEquals(true, AluUtils.isBit((byte) 21, 4));
        try {
            Assert.assertEquals(true, AluUtils.isBit((byte) 5, 8));
            Assert.fail("Failed. Should throw exception");
        } catch (IllegalArgumentException ex) {
        }
        try {
            Assert.assertEquals(true, AluUtils.isBit((byte) 5, -8));
            Assert.fail("Failed. Should throw exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testLhTo16Bit() {
        Assert.assertEquals(17441, AluUtils.lhTo16Bit((byte) 33, (byte) 68));
    }
    
    @Test
    public void testi16ToHex() {
        Assert.assertEquals("0200", AluUtils.i16ToHexString(0x200));
        Assert.assertEquals("8231", AluUtils.i16ToHexString(0x898231));
        Assert.assertEquals("2345", AluUtils.i16ToHexString(0x2345));
    }
}
