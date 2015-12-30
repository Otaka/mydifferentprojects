package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class CmpTest extends NesAbstractTst {

    @Test
    public void testCmpA_gt_M() {
        String[] lines;
        lines = new String[]{
            "lda #$45",
            "cmp #$8",};
        testAlu(lines, 0x45, 0x00, 0, 0xfd, 0x605, true, false, false, false);
    }

    @Test
    public void testCmpA_eq_M() {
        String[] lines;
        lines = new String[]{
            "lda #$45",
            "cmp #$45",};
        testAlu(lines, 0x45, 0x00, 0, 0xfd, 0x605, true, true, false, false);
    }

    @Test
    public void testCmpA_ls_M() {
        String[] lines;
        lines = new String[]{
            "lda #$44",
            "cmp #$45",};
        testAlu(lines, 0x44, 0x00, 0, 0xfd, 0x605, false, false, true, false);
    }

    @Test
    public void testBit() {
        String[] lines;
        lines = new String[]{
            "lda #$c0",
            "sta $01",
            "lda #$10",
            "bit $01"
        };
        testAlu(lines, 0x10, 0x00, 0, 0xfd, 0x609, false, true, true, true);

        lines = new String[]{
            "lda #$44",
            "sta $01",
            "lda #$70",
            "bit $01"
        };
        testAlu(lines, 0x70, 0x00, 0, 0xfd, 0x609, false, false, false, true);

        lines = new String[]{
            "lda #$6",
            "sta $01",
            "lda #$18",
            "bit $01"
        };
        testAlu(lines, 0x18, 0x00, 0, 0xfd, 0x609, false, true, false, false);

        lines = new String[]{
            "lda #$6",
            "sta $01",
            "lda #$c",
            "bit $01"
        };
        testAlu(lines, 0xc, 0x00, 0, 0xfd, 0x609, false, false, false, false);
    }

}
