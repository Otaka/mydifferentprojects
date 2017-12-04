package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class JsrTest extends NesAbstractTst {

    @Test
    public void testJsr() {
        String[] lines;
        lines = new String[]{
            "jsr procX",
            "  ldx #$4",
            "  brk",
            "procX:",
            "  lda #$45",
            "  rts"
        };
        testAlu(lines, 0x45, 0x04, 0x0, 0xfd, 0x606, false, false, false, false);
        lines = new String[]{
            "jsr procX",
            "  lda #$43",
            "  brk",
            "procX:",
            "  ldx #$44",
            "  jsr procY",
            "  rts",
            "procY:",
            "  ldy #$45",
            "  rts",
            "  lda #$56"
        };
        testAlu(lines, 0x43, 0x44, 0x45, 0xfd, 0x606, false, false, false, false);
    }
}
