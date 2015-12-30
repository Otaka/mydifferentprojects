package com.nes.processor.memory_addressing;

import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public abstract class AbstractMemoryAdressing {

    public byte getValue(ALU alu) {
        return 0;
    }

    public void setValue(ALU alu, byte value) {
    }

    public int getZeroPageAddress(int address) {
        return address>0xff?address&0xff:address;
    }

    public void calculateIndex(ALU alu) {
    }

    public byte[] parseFromString(String arg) {
        return null;
    }

    public String getLabel(String label) {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract int getPriority();

    public abstract String getStringDefinition(ALU alu);
}
