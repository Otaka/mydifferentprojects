package com.nes.processor;

import com.nes.NesAbstractTst;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ShiftsTest extends NesAbstractTst {

    @Test
    public void testAsl() {
        String[] lines;
        lines = new String[]{
            "lda #$89",
            "asl A",};
        testAlu(lines, 0x12, 0x00, 0, 0xfd, 0x604, true, false, false, false);

        lines = new String[]{
            "lda #$F2",
            "asl A",};
        testAlu(lines, 0xe4, 0x00, 0, 0xfd, 0x604, true, false, true, false);

        lines = new String[]{
            "lda #$64",
            "asl A",};
        testAlu(lines, 0xc8, 0x00, 0, 0xfd, 0x604, false, false, true, false);
    }

    @Test
    public void testLsr() {
        String[] lines;
        lines = new String[]{
            "lda #$66",
            "lsr A",};
        testAlu(lines, 0x33, 0x00, 0, 0xfd, 0x604, false, false, false, false);

        lines = new String[]{
            "lda #$33",
            "lsr A",};
        testAlu(lines, 0x19, 0x00, 0, 0xfd, 0x604, true, false, false, false);

        lines = new String[]{
            "lda #$ff",
            "lsr A",};
        testAlu(lines, 0x7f, 0x00, 0, 0xfd, 0x604, true, false, false, false);
    }

    @Test
    public void testRol() {
        String[] lines;
        lines = new String[]{
            "lda #$ff",
            "rol A",};
        testAlu(lines, 0xfe, 0x00, 0, 0xfd, 0x604, true, false, true, false);

        lines = new String[]{
            "sec",
            "lda #$ff",
            "rol A",};
        testAlu(lines, 0xff, 0x00, 0, 0xfd, 0x605, true, false, true, false);

        lines = new String[]{
            "lda #$7f",
            "rol A",};
        testAlu(lines, 0xfe, 0x00, 0, 0xfd, 0x604, false, false, true, false);

        lines = new String[]{
            "sec",
            "lda #$7f",
            "rol A",};
        testAlu(lines, 0xff, 0x00, 0, 0xfd, 0x605, false, false, true, false);

        lines = new String[]{
            //"sec",
            "lda #$4",
            "rol A",};
        testAlu(lines, 0x08, 0x00, 0, 0xfd, 0x604, false, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$4",
            "rol A",};
        testAlu(lines, 0x09, 0x00, 0, 0xfd, 0x605, false, false, false, false);
    }

    @Test
    public void testRor() {
        String[] lines;
        lines = new String[]{
            "lda #$ff",
            "ror A",};
        testAlu(lines, 0x7f, 0x00, 0, 0xfd, 0x604, true, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$ff",
            "ror A",};
        testAlu(lines, 0xff, 0x00, 0, 0xfd, 0x605, true, false, true, false);

        lines = new String[]{
            "lda #$7f",
            "ror A",};
        testAlu(lines, 0x3f, 0x00, 0, 0xfd, 0x604, true, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$7f",
            "ror A",};
        testAlu(lines, 0xbf, 0x00, 0, 0xfd, 0x605, true, false, true, false);

        lines = new String[]{
            //"sec",
            "lda #$4",
            "ror A",};
        testAlu(lines, 0x02, 0x00, 0, 0xfd, 0x604, false, false, false, false);

        lines = new String[]{
            "sec",
            "lda #$4",
            "ror A",};
        testAlu(lines, 0x82, 0x00, 0, 0xfd, 0x605, false, false, true, false);

        lines = new String[]{
            "lda #$1",
            "ror A",};
        testAlu(lines, 0x0, 0x00, 0, 0xfd, 0x604, true, true, false, false);

        lines = new String[]{
            "sec",
            "lda #$1",
            "ror A",};
        testAlu(lines, 0x80, 0x00, 0, 0xfd, 0x605, true, false, true, false);
    }
}
