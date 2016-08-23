package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.MemoryAddressing16Result;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class Adressing16BitTest {

    public Adressing16BitTest() {
    }

    @Test
    public void testParseSimpleAddressing() {
        Adressing16Bit addressing = new Adressing16Bit();
        MemoryAddressing16Result result = (MemoryAddressing16Result) addressing.parse("[bx +si]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0, result.getRm());
        result = (MemoryAddressing16Result) addressing.parse("[si + bx]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[bx+di]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b001, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[bp+si]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b010, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[bp+di]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b011, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[si]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b100, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[di]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b101, result.getRm());

        result = (MemoryAddressing16Result) addressing.parse("[8]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b110, result.getRm());
        assertEquals(8, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[987]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b110, result.getRm());
        assertEquals(987, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[0x987]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b110, result.getRm());
        assertEquals(2439, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[bx]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0b111, result.getRm());
    }

    @Test
    public void testParseWrongIncoming() {
        Adressing16Bit addressing = new Adressing16Bit();
        assertNull(addressing.parse("[bx+bx]", new Context().setBits(16)));
        assertNull(addressing.parse("[bx+bx", new Context().setBits(16)));
        assertNull(addressing.parse("kjkj", new Context().setBits(16)));
    }

    @Test
    public void testParseOffsetAddressing() {
        Adressing16Bit addressing = new Adressing16Bit();
        MemoryAddressing16Result result = (MemoryAddressing16Result) addressing.parse("[bx +si+54]", new Context().setBits(16));
        assertEquals(0b01, result.getMod());
        assertEquals(0b000, result.getRm());
        assertEquals(54, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[98+bp]", new Context().setBits(16));
        assertEquals(0b01, result.getMod());
        assertEquals(0b0110, result.getRm());
        assertEquals(98, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[989+bp]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b0110, result.getRm());
        assertEquals(989, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[989+si+bx]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b000, result.getRm());
        assertEquals(989, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[989+di+bx]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b001, result.getRm());
        assertEquals(989, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[989+si+bp]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b010, result.getRm());
        assertEquals(989, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[989+di+bp]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b011, result.getRm());
        assertEquals(989, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[si+0x1000]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b100, result.getRm());
        assertEquals(0x1000, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[di+0x1000]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b101, result.getRm());
        assertEquals(0x1000, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[bp+0x1000]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b110, result.getRm());
        assertEquals(0x1000, result.getOffset());

        result = (MemoryAddressing16Result) addressing.parse("[bx+0x1000]", new Context().setBits(16));
        assertEquals(0b10, result.getMod());
        assertEquals(0b111, result.getRm());
        assertEquals(0x1000, result.getOffset());
    }
}
