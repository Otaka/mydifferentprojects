package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * Indirect is only used by Jmp, that is why lets jump to read command itself
 * 
 */
public class INDIRECT extends AbstractMemoryAdressing {

    private final RegExpExtractor extractor = new RegExpExtractor("\\(\\s*\\$([A-Za-z0-9]{1,4})\\s*\\)");

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public byte[] parseFromString(String arg) {
        extractor.startMatcher(arg);
        if (!extractor.nextMatch()) {
            return null;
        }
        String value = extractor.groups()[0];
        short shValue = (short) Integer.parseInt(value, 16);
        byte l = (byte) (shValue & 0xFF);
        byte h = (byte) ((shValue >> 8) & 0xFF);
        return new byte[]{l, h};
    }

    @Override
    public String getStringDefinition(ALU alu) {
        StringBuilder sb = new StringBuilder();
        byte l = alu.getNextByte();
        byte h = alu.getNextByte();
        sb.append("($").append(AluUtils.byteToHexString(h)).append(AluUtils.byteToHexString(l)).append(")");
        sb.append(" = ");
        int value = AluUtils.lhTo16Bit(l, h);
        byte memL = alu.getMemory().getValue(value);
        byte memH = alu.getMemory().getValue(value + 1);
        int memValue = AluUtils.lhTo16Bit(memL, memH);
        sb.append(AluUtils.i16ToHexString(memValue));
        return sb.toString();
    }
}
