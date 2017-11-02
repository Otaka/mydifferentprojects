package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class ABS extends AbstractMemoryAdressing {
    int index = 0;

    private final RegExpExtractor extractor = new RegExpExtractor("\\$([A-Za-z0-9]{1,4})");

    @Override
    public void calculateIndex(ALU alu) {
        byte l = alu.getNextByte();
        byte h = alu.getNextByte();
        index = AluUtils.lhTo16Bit(l, h);
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
        }
        String value = extractor.groups()[0];
        short shValue = (short) Integer.parseInt(value, 16);
        byte l = (byte) (shValue & 0xFF);
        byte h = (byte) ((shValue >> 8) & 0xFF);
        return new byte[]{l, h};
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        calculateIndex(alu);
        byte value=getValue(alu);
        //alu.getMemory().printValues(0xa900, 255);
        return "$"+AluUtils.i16ToHexString(index)+" = "+AluUtils.byteToHexString(value);
    }
}
