package com.nes.ppu;

import com.nes.rom.Rom;

/**
 * @author sad
 */
public class Ppu {
    private Rom rom;
    private byte[] memory = new byte[0x4000];

    public void setValue(byte value, int address) {
        memory[address] = value;
    }

    public byte getValue(int address) {
        return memory[address];
    }

    public byte readRegister(int register) {
        return (byte)0xFF;
    }

    public void writeRegister(int register, byte value) {
    }
    
    public void loadChrFromRom(Rom rom){
        
    }
}
