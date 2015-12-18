package com.macro;

import com.macro.tokenizer.Tokenizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author sad
 */
public class TokenizerTest {

    public TokenizerTest() {
    }

    @Test
    public void testBasicCheck() {
        Tokenizer t = new Tokenizer("asd  343 \t klj");
        assertEquals("asd", t.nextToken().getValue());
        assertEquals("  ", t.nextToken().getValue());
        assertEquals("343", t.nextToken().getValue());
        assertEquals(" \t ", t.nextToken().getValue());
        assertEquals("klj", t.nextToken().getValue());
    }

    @Test
    public void testDigits() {
        Tokenizer t = new Tokenizer("36 98.65 12365");
        assertEquals("36", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("98.65", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("12365", t.nextToken().getValue());
    }

    @Test
    public void testString() {
        Tokenizer t = new Tokenizer("somevalue \"string 1\"'kjhkjh kjh ' \"asd '1' '2'\"");
        assertEquals("somevalue", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("\"string 1\"", t.nextToken().getValue());
        assertEquals("'kjhkjh kjh '", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("\"asd '1' '2'\"", t.nextToken().getValue());
    }
    
    @Test
    public void testStringWithEscaping() {
        Tokenizer t = new Tokenizer(" \"1\\\"2\" ");
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("\"1\\\"2\"", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
    }

    @Test
    public void testSingleLineComment() {
        Tokenizer t = new Tokenizer("some string//comment ff");
        assertEquals("some", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("string", t.nextToken().getValue());
        assertEquals("//comment ff", t.nextToken().getValue());
    }

    @Test
    public void testMultiLineComment() {
        Tokenizer t = new Tokenizer("some string/*comment\nff*/");
        assertEquals("some", t.nextToken().getValue());
        assertEquals(" ", t.nextToken().getValue());
        assertEquals("string", t.nextToken().getValue());
        assertEquals("/*comment\nff*/", t.nextToken().getValue());
    }

    @Test
    public void testDifferentChars() {
        Tokenizer t = new Tokenizer("myword(sd)*65^34@#$%^/&");
        assertEquals("myword", t.nextToken().getValue());
        assertEquals("(", t.nextToken().getValue());
        assertEquals("sd", t.nextToken().getValue());
        assertEquals(")", t.nextToken().getValue());
        assertEquals("*", t.nextToken().getValue());
        assertEquals("65", t.nextToken().getValue());
        assertEquals("^", t.nextToken().getValue());
        assertEquals("34", t.nextToken().getValue());
        assertEquals("@", t.nextToken().getValue());
        assertEquals("#", t.nextToken().getValue());
        assertEquals("$", t.nextToken().getValue());
        assertEquals("%", t.nextToken().getValue());
        assertEquals("^", t.nextToken().getValue());
        assertEquals("/", t.nextToken().getValue());
        assertEquals("&", t.nextToken().getValue());
    }
}