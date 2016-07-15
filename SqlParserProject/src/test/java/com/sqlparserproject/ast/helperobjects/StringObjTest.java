package com.sqlparserproject.ast.helperobjects;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class StringObjTest {

    @Test
    public void testToString() {
        StringObj strObj = new StringObj("aaa", "bbb");
        assertEquals("bbb:aaa", strObj.toString());
    }

}
