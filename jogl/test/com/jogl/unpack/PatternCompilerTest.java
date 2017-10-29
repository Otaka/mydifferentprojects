package com.jogl.unpack;

import java.io.*;
import java.util.List;
import org.junit.*;

/**
 *
 * @author Dmitry
 */
public class PatternCompilerTest {

    public PatternCompilerTest() {
    }

    @Before
    public void setUp() {
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value),
            (byte) (value >>> 8),
            (byte) (value >>> 16),
            (byte) (value >>> 24)};
    }

    public byte[] longToByteArray(long value) {
        return new byte[]{
            (byte) value,
            (byte) (value >>> 8),
            (byte) (value >>> 16),
            (byte) (value >>> 24),
            (byte) (value >>> 32),
            (byte) (value >>> 40),
            (byte) (value >>> 48),
            (byte) (value >>> 56)
        };
    }

    @Test
    public void testCompilePattern() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write("My string!".getBytes("cp1251"));
        byteStream.write(0);
        byteStream.write("secondStream".getBytes("cp1251"));
        byteStream.write(0);

        byteStream.write('b');
        byteStream.write(intToByteArray(1));
        byteStream.write(intToByteArray(50));
        byteStream.write(intToByteArray(9999987));
        byte[] buffer = byteStream.toByteArray();
        PackPattern pattern = new PatternCompiler().compilePattern("Z*Z*Zl*");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals("My string!", values.get(0));
        Assert.assertEquals("secondStream", values.get(1));
        Assert.assertEquals('b', values.get(2));
        Assert.assertEquals(1, values.get(3));
        Assert.assertEquals(50, values.get(4));
        Assert.assertEquals(9999987, values.get(5));
    }

    @Test
    public void testZStringWithFixedSize() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write("someString".getBytes("cp1251"));
        byte[] buffer = byteStream.toByteArray();
        PackPattern pattern = new PatternCompiler().compilePattern("Z10");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals("someString", values.get(0));

        byteStream.reset();
        byteStream.write("someString".getBytes("cp1251"));
        byteStream.write(new byte[25]);//zeros
        byteStream.write(intToByteArray(65456));
        buffer = byteStream.toByteArray();
        pattern = new PatternCompiler().compilePattern("Z[35] l");
        values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals("someString", values.get(0));
        Assert.assertEquals(65456, values.get(1));
    }

    @Test
    public void testFloat() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(intToByteArray(Float.floatToIntBits(654.6565f)));
        byteStream.write(intToByteArray(Float.floatToIntBits(-9874.65f)));
        byteStream.write(intToByteArray(Float.floatToIntBits(98978.6546f)));
        byte[] buffer = byteStream.toByteArray();
        PackPattern pattern = new PatternCompiler().compilePattern("f*");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals(654.6565f, values.get(0));
        Assert.assertEquals(-9874.65f, values.get(1));
        Assert.assertEquals(98978.6546f, values.get(2));
    }

    @Test
    public void testInt() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(intToByteArray(-999999999));
        byteStream.write(intToByteArray(999999999));
        byteStream.write(intToByteArray(Integer.MAX_VALUE));
        byteStream.write(intToByteArray(Integer.MIN_VALUE));

        byte[] buffer = byteStream.toByteArray();
        PackPattern pattern = new PatternCompiler().compilePattern("i[4]");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals(-999999999, values.get(0));
        Assert.assertEquals(999999999, values.get(1));
        Assert.assertEquals(Integer.MAX_VALUE, values.get(2));
        Assert.assertEquals(Integer.MIN_VALUE, values.get(3));
    }

    @Test
    public void testLong() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(longToByteArray(-99999));
        byteStream.write(longToByteArray(99999));
        byteStream.write(longToByteArray(Long.MIN_VALUE));
        byteStream.write(longToByteArray(Long.MAX_VALUE));
        byte[] buffer = byteStream.toByteArray();

        PackPattern pattern = new PatternCompiler().compilePattern("q*");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals(-99999l, values.get(0));
        Assert.assertEquals(99999l, values.get(1));
        Assert.assertEquals(Long.MIN_VALUE, values.get(2));
        Assert.assertEquals(Long.MAX_VALUE, values.get(3));
    }

    @Test
    public void testDouble() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(longToByteArray(Double.doubleToLongBits(343434.6565)));
        byteStream.write(longToByteArray(Double.doubleToLongBits(-343434.6565)));
        byteStream.write(longToByteArray(Double.doubleToLongBits(Double.MAX_VALUE)));
        byteStream.write(longToByteArray(Double.doubleToLongBits(-Double.MAX_VALUE)));

        byte[] buffer = byteStream.toByteArray();

        PackPattern pattern = new PatternCompiler().compilePattern("d*");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals(343434.6565, values.get(0));
        Assert.assertEquals(-343434.6565, values.get(1));
        Assert.assertEquals(Double.MAX_VALUE, values.get(2));
        Assert.assertEquals(-Double.MAX_VALUE, values.get(3));
    }

    @Test
    public void testSkip() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(longToByteArray(Double.doubleToLongBits(343434.6565)));
        byteStream.write(intToByteArray(6565));
        byteStream.write(new byte[6]);
        byteStream.write(intToByteArray(-6456321));

        byte[] buffer = byteStream.toByteArray();

        PackPattern pattern = new PatternCompiler().compilePattern("dix[6]i");
        List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
        Assert.assertEquals(343434.6565, values.get(0));
        Assert.assertEquals(6565, values.get(1));
        Assert.assertEquals(-6456321, values.get(2));
    }

    @Test
    public void testLongL() throws UnsupportedEncodingException, IOException {
        try (InputStream stream = PatternCompilerTest.class.getResourceAsStream("testfile.tst")) {
            byte[] buffer = new byte[64];
            stream.read(buffer);

            PackPattern pattern = new PatternCompiler().compilePattern("LLLLLLLLLL");
            List values = Unpack.unpack(new ByteArrayInputStream(buffer), pattern);
            Assert.assertEquals(1419286629l, values.get(0));
            Assert.assertEquals(1988712200l, values.get(1));
            Assert.assertEquals(2272880644l, values.get(2));
            Assert.assertEquals(1703364182l, values.get(3));
            Assert.assertEquals(1678268227l, values.get(4));
            Assert.assertEquals(1181058912l, values.get(5));
            Assert.assertEquals(912544896l, values.get(6));
            Assert.assertEquals(1665164624l, values.get(7));
            Assert.assertEquals(1682268244l, values.get(8));
            Assert.assertEquals(840189544l, values.get(9));
        }
    }
}
