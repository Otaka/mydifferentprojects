package com.nwn.data.tpc;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class TpcDecompressTest {

    public TpcDecompressTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testInterpolation() throws Exception {
        Assert.assertEquals(0xb4888e9f, TpcDecompress.interpolate32(0.3, 0xaabbccdd, 0xcc120011));
        Assert.assertEquals(0xcc120011, TpcDecompress.interpolate32(1, 0xaabbccdd, 0xcc120011));
        Assert.assertEquals(0xaabbccdd, TpcDecompress.interpolate32(0, 0xaabbccdd, 0xcc120011));
        Assert.assertEquals(0x7f7f7f7f, TpcDecompress.interpolate32(0.5, 0x00000000, 0xffffffff));
    }

    @Test
    public void testConvert565To8888() throws Exception {
        Assert.assertEquals(0x8ff, TpcDecompress.convert565To8888(1));
        Assert.assertEquals(0xf8fcf8ff, TpcDecompress.convert565To8888(0xffff));
        Assert.assertEquals(0xb460ff, TpcDecompress.convert565To8888(0x05ac));
        Assert.assertEquals(0x18fc88ff, TpcDecompress.convert565To8888(0x1ff1));
        Assert.assertEquals(0x389818ff, TpcDecompress.convert565To8888(0x3cc3));
    }
}
