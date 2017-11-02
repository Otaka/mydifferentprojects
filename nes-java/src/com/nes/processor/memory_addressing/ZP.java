package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class ZP extends AbstractMemoryAdressing {

    private int index;
    private final RegExpExtractor extractor = new RegExpExtractor("\\$([0-9A-F]{1,2})");

    @Override
    public void calculateIndex(ALU alu) {
        index = AluUtils.unsignedByte(alu.getNextByte());
        index = getZeroPageAddress(index);
    }

    @Override
    public byte getValue(ALU alu) {
        return alu.getMemory().getValue(index);
    }

    @Override
    public void setValue(ALU alu, byte value) {
        alu.getMemory().setValue(value, index);
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
        return 0;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        calculateIndex(alu);
        byte value = (byte)index;
        return "$" + AluUtils.byteToHexString(value)+" = "+AluUtils.byteToHexString(getValue(alu));
    }
}
