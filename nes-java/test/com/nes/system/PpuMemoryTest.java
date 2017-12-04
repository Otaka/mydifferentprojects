package com.nes.system;

import com.nes.ppu.Ppu;
import com.nes.ppu.SpriteSize;
import com.nes.processor.NesMemory;
import com.nes.rom.Rom;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author sad
 */
public class PpuMemoryTest {

    private final int PPU_CTRL = 0x2000;
    private final int PPU_MASK = 0x2001;
    private final int PPU_STATUS = 0x2002;
    private final int OAM_ADDR = 0x2003;
    private final int OAM_DATA = 0x2004;
    private final int PPU_SCROLL = 0x2005;
    private final int PPU_ADDR = 0x2006;
    private final int PPU_DATA = 0x2007;
    private final int OAM_DMA = 0x4014;

    @Test
    public void ppuRegistersWriteTest() {
        Ppu ppu = new Ppu();
        NesMemory nesMemory = new NesMemory(ppu, new Rom());

        nesMemory.setValue(209, PPU_CTRL);
        Assert.assertEquals((byte) 209, ppu.getPpuCtrl());

        nesMemory.setValue(126, PPU_MASK);
        Assert.assertEquals((byte) 126, ppu.getPpuMask());

        nesMemory.setValue(59, PPU_STATUS);
        Assert.assertEquals((byte) 59, ppu.getPpuStatus());

        nesMemory.setValue(225, OAM_ADDR);
        Assert.assertEquals((byte) 225, ppu.getOamAddr());

        nesMemory.setValue(136, PPU_SCROLL);
        Assert.assertEquals((byte) 136, ppu.getPpuScroll());

        nesMemory.setValue(78, PPU_ADDR);
        Assert.assertEquals((byte) 78, ppu.getPpuAddr());

        nesMemory.setValue(25, OAM_DMA);
        Assert.assertEquals((byte) 25, ppu.getOamDma());
    }

    @Test
    public void testOamData() {
        Ppu ppu = new Ppu();
        NesMemory nesMemory = new NesMemory(ppu, new Rom());
        byte[] testOam = new byte[256];
        Assert.assertArrayEquals(testOam, ppu.getOam());

        //check if addr is incremented after writing
        testOam[1] = 2;
        nesMemory.setValue(0x01, OAM_ADDR);
        nesMemory.setValue(0x2, OAM_DATA);
        Assert.assertArrayEquals(testOam, ppu.getOam());
        Assert.assertEquals(2, ppu.getOamAddr());
        
        testOam[2] = 3;
        nesMemory.setValue(0x3, OAM_DATA);
        Assert.assertArrayEquals(testOam, ppu.getOam());
        Assert.assertEquals(3, ppu.getOamAddr());
        
        testOam[3] = 4;
        nesMemory.setValue(0x4, OAM_DATA);
        Assert.assertArrayEquals(testOam, ppu.getOam());
        Assert.assertEquals(4, ppu.getOamAddr());
        
        nesMemory.setValue(0x3, OAM_ADDR);
        Assert.assertEquals((byte)4, nesMemory.getValue(OAM_DATA));
        Assert.assertEquals((byte)3, nesMemory.getValue(OAM_ADDR));//addr not moved after reading
    }

    @Test
    public void testPpuCtrl() {
        Ppu ppu = new Ppu();
        NesMemory nesMemory = new NesMemory(ppu, new Rom());
        //base name table address
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(0x2000, ppu.getBaseNameTableAddress());
        nesMemory.setValue(0b00000001, PPU_CTRL);
        Assert.assertEquals(0x2400, ppu.getBaseNameTableAddress());
        nesMemory.setValue(0b00000010, PPU_CTRL);
        Assert.assertEquals(0x2800, ppu.getBaseNameTableAddress());
        nesMemory.setValue(0b00000011, PPU_CTRL);
        Assert.assertEquals(0x2C00, ppu.getBaseNameTableAddress());
        //sprite table
        nesMemory.setValue(0b00001000, PPU_CTRL);
        Assert.assertEquals(0x1000, ppu.getSpritePatternTableAddress());
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(0x0000, ppu.getSpritePatternTableAddress());
        //background table
        nesMemory.setValue(0b00010000, PPU_CTRL);
        Assert.assertEquals(0x1000, ppu.getBackgroundPatternTableAddress());
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(0x0000, ppu.getBackgroundPatternTableAddress());
        //sprite size
        nesMemory.setValue(0b00100000, PPU_CTRL);
        Assert.assertEquals(SpriteSize._8x16, ppu.getSpriteSize());
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(SpriteSize._8x8, ppu.getSpriteSize());
        //master slave
        nesMemory.setValue(0b01000000, PPU_CTRL);
        Assert.assertEquals(true, ppu.isMaster());
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(false, ppu.isMaster());
        //Nmi
        nesMemory.setValue(0b10000000, PPU_CTRL);
        Assert.assertEquals(true, ppu.isNmiEnabled());
        nesMemory.setValue(0b00000000, PPU_CTRL);
        Assert.assertEquals(false, ppu.isNmiEnabled());
    }

    @Test
    public void testPpuMask() {
        Ppu ppu = new Ppu();
        NesMemory nesMemory = new NesMemory(ppu, new Rom());
        //base name table address
        nesMemory.setValue(0b00000000, PPU_MASK);
        Assert.assertEquals(false, ppu.showBackground());
        Assert.assertEquals(false, ppu.showSprites());
        Assert.assertEquals(false, ppu.isGrayscale());

        nesMemory.setValue(0b00000001, PPU_MASK);
        Assert.assertEquals(true, ppu.isGrayscale());

        nesMemory.setValue(0b00001000, PPU_MASK);
        Assert.assertEquals(true, ppu.showBackground());

        nesMemory.setValue(0b00010000, PPU_MASK);
        Assert.assertEquals(true, ppu.showSprites());
    }

}
