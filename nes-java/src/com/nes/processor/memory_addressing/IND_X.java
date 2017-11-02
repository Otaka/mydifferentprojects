package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class IND_X extends AbstractMemoryAdressing {
    private final RegExpExtractor extractor = new RegExpExtractor("\\(\\$([0-9A-F]{1,2})\\s*,\\s*X\\s*\\)");
    int index = 0;

    int readedIndex;
    int readedIndexPlusX;
    byte l;
    byte h;

    @Override
    public void calculateIndex(ALU alu) {
        readedIndex = AluUtils.unsignedByte(alu.getNextByte());
        readedIndexPlusX = readedIndex + AluUtils.unsignedByte(alu.getX());
        l = alu.getMemory().getValue(getZeroPageAddress(readedIndexPlusX));
        h = alu.getMemory().getValue(getZeroPageAddress(readedIndexPlusX + 1));
        index = AluUtils.lhTo16Bit(l, h);

        //l = alu.getMemory().getValue(index);
        // h = alu.getMemory().getValue(index + 1);
        //index = AluUtils.lhTo16Bit(l, h);
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
        return 7;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        calculateIndex(alu);
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append('$').append(AluUtils.byteToHexString((byte) readedIndex));
        sb.append(",X) @ ");
        sb.append(AluUtils.byteToHexString((byte) readedIndexPlusX));
        sb.append(" = ");
        sb.append(AluUtils.byteToHexString(h)).append(AluUtils.byteToHexString(l));
        byte value = getValue(alu);
        sb.append(" = ").append(AluUtils.byteToHexString(value));
        return sb.toString();
    }
}
