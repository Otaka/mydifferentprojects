package com.sqlparserproject.ast;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class NumberAstTest {

    @Test
    public void testToString() {
        NumberAst number = new NumberAst("123");
        assertEquals("123", number.toString());
        StringAst string = new StringAst("123");
        assertEquals("123", string.toString());
        BinaryOperation binary = new BinaryOperation("+", new SimpleFieldAst("n", "c"), new SimpleFieldAst("a", "b"));
        assertEquals("+", binary.toString());
        TableNameAst table = new TableNameAst("mytable");
        assertEquals("mytable", table.toString());
        
    }
}
