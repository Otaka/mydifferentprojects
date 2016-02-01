package com;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class StringCompareUtilsTest {

    public StringCompareUtilsTest() {
    }

    @Test
    public void testLike2Parts() {
        assertTrue(StringCompareUtils.like2Parts("myTestString", "my", "String", true, true));
        assertFalse(StringCompareUtils.like2Parts("1myTestString", "my", "String", true, true));
        assertFalse(StringCompareUtils.like2Parts("myTestString2", "my", "String", true, true));
        assertTrue(StringCompareUtils.like2Parts("1myTestString1", "my", "String", false, false));
        assertFalse(StringCompareUtils.like2Parts("1miTestString1", "my", "String", false, false));
        assertFalse(StringCompareUtils.like2Parts("1mTestStri3ng1", "my", "String", false, false));
    }

    @Test
    public void testLike3Parts() {
        assertFalse(StringCompareUtils.like3Parts("123456789", "1", "67", "789", true, true));
        assertTrue(StringCompareUtils.like3Parts("123456789", "1", "67", "89", true, true));
        assertFalse(StringCompareUtils.like3Parts("123456789", "4", "67", "89", true, true));
        assertTrue(StringCompareUtils.like3Parts("123456789", "4", "67", "89", false, true));
        assertFalse(StringCompareUtils.like3Parts("123456789", "4", "67", "8b", false, true));
        assertFalse(StringCompareUtils.like3Parts("123456789", "4", "67", "8b", false, false));
        assertFalse(StringCompareUtils.like3Parts("123456789", "4b", "67", "8b", false, false));
        assertFalse(StringCompareUtils.like3Parts("123456789", "1", "6b", "89", false, false));
        assertFalse(StringCompareUtils.like3Parts("1", "1", "6b", "89", false, false));
    }

    @Test
    public void testLikeNParts() {
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "1", "3", "6", "78b"));
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "1", "3", "6b", "789"));
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "b", "3", "67", "789"));
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "b", "3", "67", "78"));
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "b", "3", "6", "78b"));
        assertFalse(StringCompareUtils.likeNParts("123456789", true, true, "1", "3", "67", "789"));
        assertTrue(StringCompareUtils.likeNParts("123456789", true, true, "1", "3", "6", "789"));
        assertTrue(StringCompareUtils.likeNParts("123456789", true, false, "1", "3", "6", "789"));
        assertTrue(StringCompareUtils.likeNParts("b123456789", false, false, "1", "3", "6", "789"));
    }
    
    @Test
    public void testLikeRegexp() {
        assertTrue(StringCompareUtils.likeRegexp("myProject", "myP"));
        assertFalse(StringCompareUtils.likeRegexp("myproject", "myP"));
        assertTrue(StringCompareUtils.likeRegexp("myProject", "myP"));
        assertTrue(StringCompareUtils.likeRegexp("myPro68", "myP.*\\d+"));
    }

}
