package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class IMMEDIATE extends AbstractMemoryAdressing {
    private final RegExpExtractor extractor = new RegExpExtractor("\\#\\$?([0-9A-F]{1,2})");

    @Override
    public byte getValue(ALU alu) {
        byte value = alu.getNextByte();
        return value;
    }

    @Override
    public byte[] parseFromString(String arg) {
        extractor.startMatcher(arg);
        if (extractor.nextMatch()) {
            return new byte[]{(byte) Integer.parseInt(extractor.groups()[0], 16)};
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        byte value=getValue(alu);
        return "#$"+AluUtils.byteToHexString(value);
    }

   
    
    
}
