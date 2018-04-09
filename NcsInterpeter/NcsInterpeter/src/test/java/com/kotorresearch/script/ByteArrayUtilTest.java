package com.kotorresearch.script;

import com.kotorresearch.script.utils.ByteArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmitry
 */
public class ByteArrayUtilTest {

    @Test
    public void testByteBufferOperations() {
        byte[] buffer = new byte[]{00, 00, 00, 0x71};
        int value = ByteArrayUtils.getIntFromByteBuffer(buffer, 0);
        assertEquals(0x71, value);

        buffer = new byte[]{0x05, 0x06, 0x07, 0x08};
        value = ByteArrayUtils.getIntFromByteBuffer(buffer, 0);
        assertEquals(0x05060708, value);

        buffer = new byte[]{0x05, 0x06};
        value = ByteArrayUtils.getShortFromByteBuffer(buffer, 0);
        assertEquals(0x0506, value);
        
        ByteArrayUtils.putShortToByteBuffer(buffer, 0x8612, 0);
        value = ByteArrayUtils.getShortFromByteBuffer(buffer, 0);
        assertEquals(0x8612, value);

        buffer = new byte[4];
        ByteArrayUtils.putIntToByteBuffer(buffer, 0x05060708, 0);
        value = ByteArrayUtils.getIntFromByteBuffer(buffer, 0);
        assertEquals(0x5060708, value);
    }
}
