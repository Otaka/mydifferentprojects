/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nes.processor;

import com.nes.NesAbstractTst;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sad
 */
public class AluFlagsTest extends NesAbstractTst {

    public AluFlagsTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetFlagByte() {
        AluFlags flags=new AluFlags();
        flags.setCarry(true);
        flags.setInterrupt(true);
        flags.setNegative(true);
        byte value=flags.getFlagByte();
        assertByteEquals((byte)-91, value);
        flags.setBreakFlag(true);
        value=flags.getFlagByte();
        assertByteEquals((byte)-75, value);
    }

    @Test
    public void testSetFlagByte() {
        AluFlags flags=new AluFlags();
        flags.setFlagByte((byte)-107);
        Assert.assertEquals(true, flags.isCarry());
        Assert.assertEquals(true, flags.isInterrupt());
        Assert.assertEquals(true, flags.isNegative());
        Assert.assertEquals(true, flags.isBreakFlag());
        Assert.assertEquals(false, flags.isDecimal());
        Assert.assertEquals(false, flags.isZero());
    }
}
