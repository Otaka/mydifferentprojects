package com.nes.ppu;

import com.nes.rom.Rom;

/**
 * @author sad
 */
public class Ppu {

    private Rom rom;
    private byte ppuCtrl = 0;
    private byte ppuMask = 0;
    private byte ppuStatus = 0;
    private byte oamAddr = 0;
    //private byte OAMDATA=0;// virtual
    private byte ppuScroll = 0;
    private byte ppuAddr = 0;
    //private byte PPUDATA=0;// virtual
    private byte oamDma = 0;
    private byte[]oam=new byte[256];

    public byte getValue(int address) {
        if (address >= 0x0 && address <= 0x1fff) {
            if (address < 0x1000) {
                return rom.getVromBanks()[0][address];
            } else {
                return rom.getVromBanks()[1][address - 0x1000];
            }
        } else if (address >= 0x2000 && address <= 0x2FFF) {
            //name table
        } else if (address >= 0x3000 && address <= 0x3EFF) {
            address -= 0x1000;//mirror 0x2000-0x2EFF
            //name table
        } else if (address >= 0x3F00 && address <= 0x3FFF) {
            address = (address - 0x3F00) % 32;
            //palette
        }

        return (byte) 0;
    }

    public byte[] getOam() {
        return oam;
    }
    
    public byte readRegister(int register) {
         switch (register) {
            case 0x2004:
                return readFromOam();
            case 0x2003:
                return oamAddr;
        }
         return (byte)0xff;
    }

    public void writeRegister(int register, byte value) {
        switch (register) {
            case 0x2000:
                ppuCtrl = value;
                break;
            case 0x2001:
                ppuMask = value;
                break;
            case 0x2002:
                ppuStatus = value;
                break;
            case 0x2003:
                oamAddr = value;
                break;
            case 0x2004:
                writeToOam(value);
                break;
            case 0x2005:
                ppuScroll = value;
                break;
            case 0x2006:
                ppuAddr = value;
                break;
            case 0x2007:
                //ppudata
                break;
            case 0x4014:
                oamDma = value;
                break;
        }
    }
    
    public void writeToOam(byte value){
        oam[oamAddr&0xFF]=value;
        oamAddr++;
    }
    
    public byte readFromOam(){
        return oam[oamAddr&0xFF];
    }

    public byte getPpuCtrl() {
        return ppuCtrl;
    }

    public byte getOamAddr() {
        return oamAddr;
    }

    public byte getOamDma() {
        return oamDma;
    }

    public byte getPpuAddr() {
        return ppuAddr;
    }

    public byte getPpuMask() {
        return ppuMask;
    }

    public byte getPpuScroll() {
        return ppuScroll;
    }

    public byte getPpuStatus() {
        return ppuStatus;
    }

    private final int[] baseNameTableAddresses = new int[]{0x2000, 0x2400, 0x2800, 0x2C00};

    public int getBaseNameTableAddress() {
        return baseNameTableAddresses[ppuCtrl & 3];
    }

    public int getSpritePatternTableAddress() {
        return ((ppuCtrl & 0x8) == 0) ? 0x0000 : 0x1000;
    }

    public int getBackgroundPatternTableAddress() {
        return ((ppuCtrl & 0b00010000) == 0) ? 0x0000 : 0x1000;
    }

    public SpriteSize getSpriteSize() {
        return ((ppuCtrl & 0b00100000) == 0) ? SpriteSize._8x8 : SpriteSize._8x16;
    }

    public boolean isMaster() {
        return ((ppuCtrl & 0b01000000) != 0);
    }

    public boolean isNmiEnabled() {
        return ((ppuCtrl & 0b10000000) != 0);
    }

    public boolean showBackground() {
        return ((ppuMask & 0b00001000) != 0);
    }

    public boolean showSprites() {
        return ((ppuMask & 0b00010000) != 0);
    }
    
    public boolean isGrayscale() {
        return ((ppuMask & 0b00000001) != 0);
    }

}
