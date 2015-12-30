package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class IND_Y extends AbstractMemoryAdressing {

    private final RegExpExtractor extractor = new RegExpExtractor("\\(\\$([0-9A-F]{1,2})\\s*\\)\\s*,\\s*Y");
    int index = 0;
    byte pointer;
    int readedIndex;
    byte l;
    byte h;

    @Override
    public void calculateIndex(ALU alu) {
        pointer = alu.getNextByte();
        readedIndex = AluUtils.unsignedByte(pointer);
        l = alu.getMemory().getValue(getZeroPageAddress(readedIndex));
        h = alu.getMemory().getValue(getZeroPageAddress(readedIndex + 1));
        int dereferenced = AluUtils.lhTo16Bit(l, h);
        index = dereferenced + AluUtils.unsignedByte(alu.getY());
        index = index & 0xffff;
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
        if (!extractor.nextMatch()) {
            return null;
        } else {
            short shValue = (short) Integer.parseInt(extractor.groups()[0], 16);
            byte l = (byte) (shValue & 0xFF);
            return new byte[]{l};
        }
    }

    @Override
    public int getPriority() {
        return 8;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        calculateIndex(alu);
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append('$').append(AluUtils.byteToHexString(pointer));
        sb.append("),Y = ");
        sb.append(AluUtils.byteToHexString(h)).append(AluUtils.byteToHexString(l));
        sb.append(" @ ");
        sb.append(AluUtils.i16ToHexString(index));
        sb.append(" = ");
        byte value = getValue(alu);
        sb.append(AluUtils.byteToHexString(value));
        return sb.toString();
    }
}
