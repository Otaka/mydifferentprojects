package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class ABS_Y extends AbstractMemoryAdressing {

    private final RegExpExtractor extractor = new RegExpExtractor("\\$([0-9A-F]{1,4})\\s*,\\s*Y");
    int index = 0;
    private int implicitOffset;

    @Override
    public void calculateIndex(ALU alu) {
        byte l = alu.getNextByte();
        byte h = alu.getNextByte();
        implicitOffset = AluUtils.lhTo16Bit(l, h);
        int y = AluUtils.unsignedByte(alu.getY());
        index = (implicitOffset + y)&0xffff;
    }

    @Override
    public byte getValue(ALU alu) {
        byte v = alu.getMemory().getValue(index);
        return v;
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
            byte h = (byte) ((shValue >> 8) & 0xFF);
            return new byte[]{l, h};
        }
    }

    @Override
    public int getPriority() {
        return 60;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        calculateIndex(alu);
        StringBuilder sb = new StringBuilder();
        sb.append("$").append(AluUtils.i16ToHexString(implicitOffset)).append(",Y @ ");
        sb.append(AluUtils.i16ToHexString(index)).append(" = ");
        byte value = getValue(alu);
        sb.append(AluUtils.byteToHexString(value));
        return sb.toString();
    }
}
