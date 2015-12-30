package com.nes.processor.memory_addressing;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;

/**
 *
 * This command is used only by Jmp and Jsr, that is why let them read memory
 * themself
 */
public class ABS16 extends AbstractMemoryAdressing {
    private final RegExpExtractor extractor = new RegExpExtractor("([A-Z][A-Z0-9]*)");

    @Override
    public byte[] parseFromString(String arg) {
        if (!arg.startsWith("$")) {
            if (extractor.match(arg)) {
                return new byte[]{0, 0};
            } else {
                return null;
            }
        }
        String value = arg.substring(1);
        if (value.length() > 4) {
            return null;
        }
        short shValue = (short) Integer.parseInt(value, 16);
        byte l = (byte) (shValue & 0xFF);
        byte h = (byte) ((shValue >> 8) & 0xFF);
        return new byte[]{l, h};
    }

    @Override
    public String getLabel(String label) {
        extractor.startMatcher(label);
        if (extractor.nextMatch()) {
            return extractor.groups()[0];
        }

        return null;
    }
    
    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public String getStringDefinition(ALU alu) {
        byte l = alu.getNextByte();
        byte hi = alu.getNextByte();
        return "$"+AluUtils.byteToHexString(hi)+AluUtils.byteToHexString(l);
    }
}
