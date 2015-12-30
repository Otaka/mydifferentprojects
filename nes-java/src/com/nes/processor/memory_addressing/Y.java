package com.nes.processor.memory_addressing;

import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public class Y extends AbstractMemoryAdressing {

    @Override
    public byte getValue(ALU alu) {
        return alu.getY();
    }

    @Override
    public void setValue(ALU alu, byte value) {
        alu.setY(value);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        //return "Y";
        return "";
    }
}
