package com.sqlparserproject.ast;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class SqlTypeTest {

    @Test
    public void testTypesMethod() {
        assertEquals("int", new SqlType.IntType().toString());
        assertEquals("varbinary", new SqlType.VarbinaryType().toString());
        assertEquals(2, new SqlType.VarcharType(2).getSize());
        assertEquals("numeric(7,3)", new SqlType.NumericType(7, 3).toString());
        assertEquals("numeric(7)", new SqlType.NumericType(7,null).toString());
    }

}
