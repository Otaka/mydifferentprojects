package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 * @author Dmitry
 */
public class REL extends AbstractMemoryAdressing {

    private final RegExpExtractor extractor = new RegExpExtractor("\\$#([0-9A-F]{1,2})");
    private final RegExpExtractor labelExractor = new RegExpExtractor("([A-Z][A-Z0-9]*)");

    @Override
    public byte getValue(ALU alu) {
        return alu.getNextByte();
    }

    @Override
    public byte[] parseFromString(String arg) {
        if (extractor.match(arg)) {
            extractor.startMatcher(arg);
            extractor.nextMatch();
            String value = extractor.groups()[0];
            return new byte[]{(byte) Integer.parseInt(value, 16)};
        } else {
            if (labelExractor.match(arg)) {
                return new byte[1];
            } else {
                return null;
            }
        }
    }

    @Override
    public String getLabel(String arg) {
        labelExractor.startMatcher(arg);
        if (labelExractor.nextMatch()) {
            return labelExractor.groups()[0];
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        byte bvalue = getValue(alu);
        int pc = alu.getPc();
        int result = pc + bvalue;
        return "$" + AluUtils.i16ToHexString(result);
    }
}
