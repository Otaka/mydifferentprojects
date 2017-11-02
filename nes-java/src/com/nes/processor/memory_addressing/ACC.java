package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public class ACC extends AbstractMemoryAdressing {

    private final RegExpExtractor extractor = new RegExpExtractor("A");

    @Override
    public byte getValue(ALU alu) {
        return alu.getA();
    }

    @Override
    public void setValue(ALU alu, byte value) {
        alu.setA(value);
    }

    @Override
    public byte[] parseFromString(String arg) {
        if (arg.isEmpty()) {
            return new byte[0];
        }

        extractor.startMatcher(arg);
        if (!extractor.nextMatch()) {
            return null;
        } else {
            return new byte[0];
        }
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        return "A";
    }
}
