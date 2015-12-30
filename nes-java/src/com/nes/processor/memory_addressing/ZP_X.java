package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class ZP_X extends AbstractMemoryAdressing {

    private int index;
    private final RegExpExtractor extractor = new RegExpExtractor("\\$([0-9A-F]{1,2})\\s*,\\s*X");
    int base;

    @Override
    public void calculateIndex(ALU alu) {
        base = AluUtils.unsignedByte(alu.getNextByte());
        int offset = AluUtils.unsignedByte(alu.getX());
        int resultAddres = base + offset;
        index = getZeroPageAddress(resultAddres);
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
        return 1;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        StringBuilder sb = new StringBuilder();
        calculateIndex(alu);
        sb.append("$")
                .append(AluUtils.byteToHexString((byte) base));
        sb.append(",X @ ")
                .append(AluUtils.byteToHexString((byte) index))
                .append(" = ")
                .append(AluUtils.byteToHexString(getValue(alu)));
        return sb.toString();
    }
}
