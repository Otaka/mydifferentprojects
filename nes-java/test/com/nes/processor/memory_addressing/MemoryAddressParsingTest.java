package com.nes.processor.memory_addressing;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class MemoryAddressParsingTest extends NesAbstractTst {

    public MemoryAddressParsingTest() {
    }

    @Test
    public void testAbsParseFromString() {
        ABS abs = new ABS();
        byte[] res = abs.parseFromString("$200");
        assertBytesEquals(new byte[]{0x00, 0x02}, res);
        res = abs.parseFromString("$010");
        assertBytesEquals(new byte[]{0x10, 0x00}, res);
        res = abs.parseFromString("$FbFe");
        assertBytesEquals(new byte[]{(byte) 0xFe, (byte) 0xFb}, res);
        res = abs.parseFromString("$FbFeFe");
        assertBytesEquals(null, res);
        res = abs.parseFromString("$12,X");
        assertBytesEquals(null, res);
    }

    @Test
    public void testAbs16ParseFromString() {
        ABS16 abs = new ABS16();
        byte[] res = abs.parseFromString("$200");
        assertBytesEquals(new byte[]{0x00, 0x02}, res);
        res = abs.parseFromString("$010");
        assertBytesEquals(new byte[]{0x10, 0x00}, res);
        res = abs.parseFromString("$FbFe");
        assertBytesEquals(new byte[]{(byte) 0xFe, (byte) 0xFb}, res);
        res = abs.parseFromString("$FbFeFe");
        assertBytesEquals(null, res);
    }

    @Test
    public void testAbs_X_ParseFromString() {
        ABS_X abs = new ABS_X();
        byte[] res = abs.parseFromString("$200,X");
        assertBytesEquals(new byte[]{0x00, 0x02}, res);
        res = abs.parseFromString("$010 , X");
        assertBytesEquals(new byte[]{0x10, 0x00}, res);
        res = abs.parseFromString("$FBFE,X");
        assertBytesEquals(new byte[]{(byte) 0xFe, (byte) 0xFb}, res);
        res = abs.parseFromString("$FBFEFE,X");
        assertBytesEquals(null, res);
    }

    @Test
    public void testAbs_Y_ParseFromString() {
        ABS_Y abs = new ABS_Y();
        byte[] res = abs.parseFromString("$200,Y");
        assertBytesEquals(new byte[]{0x00, 0x02}, res);
        res = abs.parseFromString("$010 , Y");
        assertBytesEquals(new byte[]{0x10, 0x00}, res);
        res = abs.parseFromString("$FBFE,Y");
        assertBytesEquals(new byte[]{(byte) 0xFe, (byte) 0xFb}, res);
        res = abs.parseFromString("$FBFEFE,Y");
        assertBytesEquals(null, res);
    }

    @Test
    public void testImmediateParseFromString() {
        IMMEDIATE abs = new IMMEDIATE();
        byte[] res = abs.parseFromString("#$20");
        assertBytesEquals(new byte[]{0x20}, res);
        res = abs.parseFromString("#FE");
        assertBytesEquals(new byte[]{(byte) 0xfe}, res);
        res = abs.parseFromString("#$43");
        assertBytesEquals(new byte[]{(byte) 0x43}, res);
        res = abs.parseFromString("$#456");
        assertBytesEquals(null, res);
    }

    @Test
    public void testIndXParseFromString() {
        IND_X abs = new IND_X();
        byte[] res = abs.parseFromString("($20, X)");
        assertBytesEquals(new byte[]{0x20}, res);
        res = abs.parseFromString("($FE ,X )");
        assertBytesEquals(new byte[]{(byte) 0xfe}, res);
        res = abs.parseFromString("($43,X)");
        assertBytesEquals(new byte[]{(byte) 0x43}, res);
        res = abs.parseFromString("$65,X");
        assertBytesEquals(null, res);
        res = abs.parseFromString("($65,X");
        assertBytesEquals(null, res);
        res = abs.parseFromString("($65),X");
        assertBytesEquals(null, res);
        res = abs.parseFromString("$65,X)");
        assertBytesEquals(null, res);
        res = abs.parseFromString("(#$65,X)");
        assertBytesEquals(null, res);
    }

    @Test
    public void testIndYParseFromString() {
        IND_Y abs = new IND_Y();
        byte[] res = abs.parseFromString("($20 ), Y");
        assertBytesEquals(new byte[]{0x20}, res);
        res = abs.parseFromString("($FE) ,Y");
        assertBytesEquals(new byte[]{(byte) 0xfe}, res);
        res = abs.parseFromString("($43),Y");
        assertBytesEquals(new byte[]{(byte) 0x43}, res);
        res = abs.parseFromString("$65,Y");
        assertBytesEquals(null, res);
        res = abs.parseFromString("($65,Y");
        assertBytesEquals(null, res);
        res = abs.parseFromString("($65,Y)");
        assertBytesEquals(null, res);
        res = abs.parseFromString("$65,Y)");
        assertBytesEquals(null, res);
        res = abs.parseFromString("(#$65,)Y");
        assertBytesEquals(null, res);
    }
}
