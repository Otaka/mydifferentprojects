package com.kotorresearch.script.assembler;

import com.kotorresearch.script.utils.ByteArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmitry
 */
public class NcsAssemblerTest {

    @Test
    public void testAssemble() {
        NcsAssembler assembler = new NcsAssembler();
        byte[] buffer = assembler.assemble(""
                + "T 00000071\n"
                + "RSADDI\n"
                + "JSR fn_00000017\n"
                + "RETN\n"
                + "fn_00000017 : RSADDI\n"
                + "CONSTS \"MAN_LIVEB_STATE\"\n"
                + "ACTION 0244, 01\n"
                + "CONSTI 00000002\n"
                + "EQUALII\n"
                + "CPDOWNSP FFFFFFF8, 0004\n"
                + "MOVSP FFFFFFFC\n"
                + "CPTOPSP FFFFFFFC, 0004\n"
                + "CPDOWNSP FFFFFFF4, 0004\n"
                + "MOVSP FFFFFFF8\n"
                + "JMP off_0000006F\n"
                + "MOVSP FFFFFFFC\n"
                + "MOVSP FFFFFFFC\n"
                + "off_0000006F: RETN", true);
        String result = ByteArrayUtils.printBuffer(buffer, 0, buffer.length, true);
        assertEquals("4E 43 53 20 56 31 2E 30 42 00 00 00 71 02 03 1E 00 00 00 00 08 20 00 02 03 04 05 00 0F 4D 41 4E 5F 4C 49 56 45 42 5F 53 54 41 54 45 05 00 02 44 01 04 03 00 00 00 02 0B 20 01 01 FF FF FF F8 00 04 1B 00 FF FF FF FC 03 01 FF FF FF FC 00 04 01 01 FF FF FF F4 00 04 1B 00 FF FF FF F8 1D 00 00 00 00 12 1B 00 FF FF FF FC 1B 00 FF FF FF FC 20 00 ", result);
    }

    @Test
    public void testAssembleActions() {
        NcsAssembler assembler = new NcsAssembler();
        byte[] buffer = assembler.assemble(""
                + "ACTION PrintString(1), 01", false);
        String result = ByteArrayUtils.printBuffer(buffer, 0, buffer.length, true);
        assertEquals("05 00 00 01 01 ", result);
    }

    @Test
    public void testSplitLineOnLabelAndDataLine() {
        NcsAssembler assembler = new NcsAssembler();
        NcsAssembler.LineAndLabel ll = assembler.splitLineOnLabelAndDataLine("T 34565");
        assertNull(ll.label);
        assertEquals("T 34565", ll.line);

        ll = assembler.splitLineOnLabelAndDataLine("MYLABEL : T 1234");
        assertEquals("MYLABEL", ll.label);
        assertEquals("T 1234", ll.line);
    }

    @Test
    public void testRemoveComment() {
        NcsAssembler assembler = new NcsAssembler();
        String line = assembler.removeComments("12/*34*/56789");
        assertEquals("12      56789", line);

        line = assembler.removeComments("1\n2/*3\n4*/56\n789");
        assertEquals("1\n2   \n   56\n789", line);

        line = assembler.removeComments("1/*2*/345/**/67/*8*/9");
        assertEquals("1     345    67     9", line);
    }

}
