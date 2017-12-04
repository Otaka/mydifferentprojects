package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class SbcTest extends NesAbstractTst {

    @Test
    public void testSbc() {
        String[] lines;
        lines = new String[]{
            "clc",
            "lda #$50",
            "sbc #$5"
        };

        testAlu(lines, 0x4a, 0x00, 0, 0xfd, 0x606, true, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$50",
            "sbc #$5"
        };

        testAlu(lines, 0x4b, 0x00, 0, 0xfd, 0x606, true, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$5",
            "sbc #$55"
        };

        testAlu(lines, 0xb0, 0x00, 0, 0xfd, 0x606, false, false, true, false);

        lines = new String[]{
            "clc",
            "lda #$80",
            "sbc #$20"
        };

        testAlu(lines, 0x5f, 0x00, 0, 0xfd, 0x606, true, false, false, true);

        lines = new String[]{
            "clc",
            "lda #$20",
            "sbc #$80"
        };

        testAlu(lines, 0x9f, 0x00, 0, 0xfd, 0x606, false, false, true, true);

        lines = new String[]{
            "sec",
            "lda #$20",
            "sbc #$80"
        };

        testAlu(lines, 0xa0, 0x00, 0, 0xfd, 0x606, false, false, true, true);
    }
}
