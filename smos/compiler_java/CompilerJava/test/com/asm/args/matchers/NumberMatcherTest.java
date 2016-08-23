package com.asm.args.matchers;

import com.asm.args.argresult.NumberResult;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class NumberMatcherTest {

    public NumberMatcherTest() {
    }

    @Test
    public void testMatch() {
        NumberMatcher matcher=new NumberMatcher(NumberMatcher.MemSize.DWORD);
        NumberResult value=matcher.match("'CAFE'");
        assertEquals(1128351301, value.getValue());
        
        value=matcher.match("01010101b");
        assertEquals(85, value.getValue());
        
        value=matcher.match("0xFFFFFFFF");
        assertEquals(0xFFFFFFFFl, value.getValue());
    }

}