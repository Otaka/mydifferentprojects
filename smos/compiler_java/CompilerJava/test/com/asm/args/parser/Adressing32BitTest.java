package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.MemoryAddressing32Result;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sad
 */
public class Adressing32BitTest {

    public Adressing32BitTest() {
    }

    @Test
    public void testParseSimpleWithoutSIBAddressing() {
        Adressing32Bit addressing = new Adressing32Bit();
        MemoryAddressing32Result result = (MemoryAddressing32Result) addressing.parse("[eax]", new Context().setBits(16));
        assertEquals(0, result.getMod());
        assertEquals(0, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[ecx]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b001, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[edx]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b010, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[ebx]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b011, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[0x656598]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b101, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[esi]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b110, result.getRm());

        result = (MemoryAddressing32Result) addressing.parse("[edi]", new Context().setBits(32));
        assertEquals(0, result.getMod());
        assertEquals(0b111, result.getRm());

    }

    @Test
    public void testParseWrongIncoming() {
        Adressing32Bit addressing = new Adressing32Bit();
        assertNull(addressing.parse("[bx+bx]", new Context().setBits(32)));
        assertNull(addressing.parse("[bx+bx", new Context().setBits(32)));
        assertNull(addressing.parse("kjkj", new Context().setBits(32)));
    }

    @Test
    public void testParseOffsetSIBAddressing() {
        Adressing32Bit addressing = new Adressing32Bit();
        MemoryAddressing32Result result = (MemoryAddressing32Result) addressing.parse("[ 65 + eax *4 + eax ]", new Context().setBits(32));
        assertEquals(0b01, result.getMod());
        assertEquals(0b000, result.getRm());
        assertEquals(54, result.getOffset());
    }

    @Test
    public void testParseOffsetWithoutSIBAddressing() {
        Adressing32Bit addressing = new Adressing32Bit();
        MemoryAddressing32Result result = (MemoryAddressing32Result) addressing.parse("[eax+54]", new Context().setBits(32));
        assertEquals(0b01, result.getMod());
        assertEquals(0b000, result.getRm());
        assertEquals(54, result.getOffset());

        result = (MemoryAddressing32Result) addressing.parse("[ecx+98]", new Context().setBits(32));
        assertEquals(0b01, result.getMod());
        assertEquals(0b001, result.getRm());
        assertEquals(98, result.getOffset());

        result = (MemoryAddressing32Result) addressing.parse("[98+edx]", new Context().setBits(32));
        assertEquals(0b01, result.getMod());
        assertEquals(0b010, result.getRm());
        assertEquals(98, result.getOffset());

        result = (MemoryAddressing32Result) addressing.parse("[98+ebx]", new Context().setBits(32));
        assertEquals(0b01, result.getMod());
        assertEquals(0b011, result.getRm());
        assertEquals(98, result.getOffset());

    }
}
