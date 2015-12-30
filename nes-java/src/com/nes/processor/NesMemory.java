package com.nes.processor;

import com.nes.ppu.Ppu;
import com.nes.rom.Rom;

/**
 * @author sad
 */
public class NesMemory extends Memory {

    private Ppu ppu;
    private byte[] ram = new byte[0x800];
    private byte[] sram = new byte[0x2000];
    private Rom rom;

    public NesMemory(Ppu ppu, Rom rom) {
        this.ppu = ppu;
        this.rom = rom;
    }
    
    protected int normalizeAddress(int address) {
        if (address >= 0 && address <= 0x1FFF) {
            return address & 0x7FF;
        }
        if (address >= 0x2000 && address <= 0x3FFF) {
            return 0x2000 + address % 8;
        }

        return address;
    }
    
    @Override
    public byte getValue(int address) {
        int normalized = normalizeAddress(address);
        if (normalized >= 0 && normalized < 0x800) {
            return ram[normalized];
        }
        if (normalized >= 0x2000 && normalized <= 0x2007) {
            return ppu.readRegister(normalized);
        }
        if (normalized >= 0x4000 && normalized < 0x4019) {
            //apu
            return 0x0;
        }
        if (normalized >= 0x4020 && normalized < 0x5FFF) {
            return rom.readExpansionRom(address);
        }
        if (normalized >= 0x6000 && normalized < 0x7FFF) {
            return sram[normalized - 0x6000];
        }
        if (normalized >= 0x8000 && normalized < 0xFFFF) {
            return rom.readPrgRom(normalized);
        }

        return (byte)0xFF;
    }

    @Override
    public void setValue(byte value, int address) {
        int normalized = normalizeAddress(address);
        if (normalized >= 0 && normalized < 0x800) {
            ram[normalized]=value;
        }
        if (normalized >= 0x2000 && normalized <= 0x2007) {
            ppu.writeRegister(normalized,value);
        }
        if (normalized >= 0x4000 && normalized < 0x4019) {
            //apu
            //return 0x0;
        }
        if (normalized >= 0x4020 && normalized < 0x5FFF) {
            rom.writeExpansionRom(address, value);
        }
        if (normalized >= 0x6000 && normalized < 0x7FFF) {
            sram[normalized - 0x6000]=value;
        }
        if (normalized >= 0x8000 && normalized < 0xFFFF) {
            rom.writePrgRom(normalized, value);
        }
    }

    @Override
    public void printValues(int address, int size) {
    }
}
