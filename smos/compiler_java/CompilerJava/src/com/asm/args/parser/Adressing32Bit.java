package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;
import com.asm.args.parser.results.MemoryAddressing32Result;
import com.asm.exceptions.ParsingException;

/**
 * @author sad
 */
public class Adressing32Bit extends BaseAddressingParser {

    private final AddressingMode[] addressingModes = new AddressingMode[]{
        new AddressingMode(0b00, 0b000, -1, new RegFormulaPart("eax")),
        new AddressingMode(0b00, 0b001, -1, new RegFormulaPart("ecx")),
        new AddressingMode(0b00, 0b010, -1, new RegFormulaPart("edx")),
        new AddressingMode(0b00, 0b011, -1, new RegFormulaPart("ebx")),
        new AddressingMode(0b00, 0b101, -1, new OffsetFormulaPart(32)),
        new AddressingMode(0b00, 0b110, -1, new RegFormulaPart("esi")),
        new AddressingMode(0b00, 0b111, -1, new RegFormulaPart("edi")),
        new AddressingMode(0b01, 0b000, 1, new RegFormulaPart("eax"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b001, 1, new RegFormulaPart("ecx"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b010, 1, new RegFormulaPart("edx"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b011, 1, new RegFormulaPart("ebx"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b101, 1, new RegFormulaPart("ebp"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b110, 1, new RegFormulaPart("esi"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b111, 1, new RegFormulaPart("edi"), new OffsetFormulaPart(8)),
        new AddressingMode(0b10, 0b000, 1, new RegFormulaPart("eax"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b001, 1, new RegFormulaPart("ecx"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b010, 1, new RegFormulaPart("edx"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b011, 1, new RegFormulaPart("ebx"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b101, 1, new RegFormulaPart("ebp"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b110, 1, new RegFormulaPart("esi"), new OffsetFormulaPart(32)),
        new AddressingMode(0b10, 0b111, 1, new RegFormulaPart("edi"), new OffsetFormulaPart(32))
    };

    private ParsedPart parsePart(String val) {
        val = val.toLowerCase();
        for (String reg : registers) {
            if (reg.equalsIgnoreCase(val)) {
                return new ParsedPart(val, reg, 0, true).setHasScale(reg.contains("*"));
            }
        }

        try {
            long offset = NumberParser.tryToParseNumber(val, 16);
            return new ParsedPart(val, null, offset, false);
        } catch (Exception ex) {
            throw new ParsingException("Cannot parse [" + val + "] as 16 bit number offset [" + ex.getMessage() + "]");
        }
    }

    @Override
    public AbstractParseResult parse(String value, Context context) {
        if (!(value.startsWith("[") && value.endsWith("]"))) {
            return null;
        }

        String shortValue = value.substring(1, value.length() - 1);//strip the '[' ']' brackets
        String[] parts = shortValue.split("\\+");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim().replaceAll(" ", "");
        }

        sortAddressingParts(parts);
        ParsedPart[] parsedParts = new ParsedPart[parts.length];
        for (int i = 0; i < parsedParts.length; i++) {
            parsedParts[i] = parsePart(parts[i]);
        }

        for (AddressingMode addressingMode : addressingModes) {
            if (isMatch(parsedParts, addressingMode)) {
                long offset = -1;
                if (addressingMode.hasOffset()) {
                    offset = parsedParts[addressingMode.getOffsetPosition()].getOffset();
                }

                return new MemoryAddressing32Result(value, addressingMode.getMod(), addressingMode.getRm(), offset);
            }
        }

        //not found regular addressing. Lets search for SIB addressing
        int sib = parseSIBAddressing(parsedParts, value);
        if (sib != -1) {
            ParsedPart offsetPart = getOffsetParsedPart(parsedParts);
            if (offsetPart == null) {
                return new MemoryAddressing32Result(value, 0b00, 0b100, 0).setSib(sib);
            } else {
                long offset = offsetPart.getOffset();
                NumberParser byteParser=new NumberParser(NumberParser.MemSize.BYTE, Boolean.TRUE);
                if(byteParser.checkLimits(offset)){
                    return new MemoryAddressing32Result(value, 0b01, 0b100, offset).setSib(sib);
                }
                NumberParser dWordParser=new NumberParser(NumberParser.MemSize.DWORD, Boolean.TRUE);
                if(dWordParser.checkLimits(offset)){
                    return new MemoryAddressing32Result(value, 0b10, 0b100, offset).setSib(sib);
                }
                throw new ParsingException("Cannot recongize adressing mode ["+value+"]. Something wrong with offset ["+offset+"]");
            }
        } else {
            return null;
        }
    }

    private ParsedPart getOffsetParsedPart(ParsedPart[] parts) {
        for (ParsedPart part : parts) {
            if (!part.isReg()) {
                return part;
            }
        }

        return null;
    }

    private String[] sibBaseRegisters = new String[]{
        "eax", "ecx", "edx", "ebx", "esp", "ebp", "esi", "edi"
    };

    private String[] sibIndexRegisters = new String[]{
        "eax", "ecx", "edx", "ebx", "  454EMPTY_NOT_USED554", "ebp", "esi", "edi"
    };

    private String[] scaleVariants = new String[]{
        "", "2", "4", "8"
    };

    private int getIndexOfElement(String[] array, String reg) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(reg)) {
                return i;
            }
        }
        return -1;
    }

    private int parseSIBAddressing(ParsedPart[] parsedParts, String originalLine) {
        //Lets check, if it is something like this [ecx+eax*2]
        if (parsedParts.length >= 2) {
            ParsedPart baseReg = parsedParts[0];
            ParsedPart indexReg = parsedParts[1];
            if (!baseReg.isReg() || !indexReg.isReg()) {
                return -1;
            }

            int baseRegIndex = getIndexOfElement(sibBaseRegisters, baseReg.getReg());
            IndexRegister index = parseIndexRegister(indexReg.getReg());
            if (index == null) {
                return -1;
            }

            int sib = ((index.getScaleValue() & 0b11) << 6) | ((index.getIndexRegValue() & 0b111) << 3) | (baseRegIndex & 0b11);
            return sib;
        }

        return -1;
    }

    private IndexRegister parseIndexRegister(String string) {
        if (!string.contains("*")) {
            return null;
        }

        String[] parts = string.split("\\*");
        if (parts.length == 1) {
            int index = getIndexOfElement(sibIndexRegisters, parts[0]);
            if (index == -1) {
                return null;
            }

            return new IndexRegister(sibIndexRegisters[index], "", 0, index);
        } else if (parts.length == 2) {
            sortAddressingParts(parts);
            int regIndex = getIndexOfElement(sibIndexRegisters, parts[0]);
            if (regIndex == -1) {
                return null;
            }
            int scaleIndex = getIndexOfElement(scaleVariants, parts[1]);
            if (scaleIndex == -1) {
                throw new RuntimeException("Scale in SIB addressing should be 2 4 or 8");
            }

            return new IndexRegister(sibIndexRegisters[regIndex], scaleVariants[scaleIndex], scaleIndex, regIndex);
        }

        return null;
    }

    private class IndexRegister {

        private String reg;
        private String scale;
        private int scaleValue;
        private int indexRegValue;

        public IndexRegister(String reg, String scale, int scaleValue, int indexRegValue) {
            this.reg = reg;
            this.scale = scale;
            this.scaleValue = scaleValue;
            this.indexRegValue = indexRegValue;
        }

        public int getIndexRegValue() {
            return indexRegValue;
        }

        public int getScaleValue() {
            return scaleValue;
        }

        public String getReg() {
            return reg;
        }

        public String getScale() {
            return scale;
        }

    }
}
