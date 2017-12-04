package com.nes.processor.memory_addressing;

import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public class IMPLISIT extends AbstractMemoryAdressing {

    @Override
    public byte[] parseFromString(String arg) {
        if (arg.isEmpty()) {
            return new byte[0];
        }

        return null;
    }
    
    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        return "";
    }
}
