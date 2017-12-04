package com.nes.processor.memory_addressing;

import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public class X extends AbstractMemoryAdressing {

    @Override
    public byte getValue(ALU alu) {
        return alu.getX();
    }

    @Override
    public void setValue(ALU alu, byte value) {
        alu.setX(value);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        //return "X";
        return "";
    }
}
