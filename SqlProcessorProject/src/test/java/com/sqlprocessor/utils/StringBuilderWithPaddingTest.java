package com.sqlprocessor.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sad
 */
public class StringBuilderWithPaddingTest {

    @Test
    public void testStringBuilderWithPadding() {
        StringBuilderWithPadding sb = new StringBuilderWithPadding("+");
        sb.println("aaa");
        sb.println("bbb");
        Assert.assertEquals("aaa\nbbb\n", sb.toString());

        sb = new StringBuilderWithPadding("+");
        sb.println("aaa");
        sb.incLevel();
        sb.println("bbb");
        sb.decLevel();
        sb.print("ccc");
        Assert.assertEquals("aaa\n+bbb\nccc", sb.toString());
    }

}
