package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;
import com.asm.args.parser.results.MemoryAddressing16Result;
import com.asm.exceptions.ParsingException;

/**
 * @author sad
 */
public class Adressing16Bit extends BaseAddressingParser {

    private final AddressingMode[] addressingModes = new AddressingMode[]{
        new AddressingMode(0b00, 0b000, -1, new RegFormulaPart("bx"), new RegFormulaPart("si")),
        new AddressingMode(0b00, 0b001, -1, new RegFormulaPart("bx"), new RegFormulaPart("di")),
        new AddressingMode(0b00, 0b010, -1, new RegFormulaPart("bp"), new RegFormulaPart("si")),
        new AddressingMode(0b00, 0b011, -1, new RegFormulaPart("bp"), new RegFormulaPart("di")),
        new AddressingMode(0b00, 0b100, -1, new RegFormulaPart("si")),
        new AddressingMode(0b00, 0b101, -1, new RegFormulaPart("di")),
        new AddressingMode(0b00, 0b110, 0, new OffsetFormulaPart(16)),
        new AddressingMode(0b00, 0b111, -1, new RegFormulaPart("bx")),
        new AddressingMode(0b01, 0b000, 2, new RegFormulaPart("bx"), new RegFormulaPart("si"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b001, 2, new RegFormulaPart("bx"), new RegFormulaPart("di"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b010, 2, new RegFormulaPart("bp"), new RegFormulaPart("si"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b011, 2, new RegFormulaPart("bp"), new RegFormulaPart("di"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b100, 1, new RegFormulaPart("si"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b101, 1, new RegFormulaPart("di"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b110, 1, new RegFormulaPart("bp"), new OffsetFormulaPart(8)),
        new AddressingMode(0b01, 0b111, 1, new RegFormulaPart("bx"), new OffsetFormulaPart(8)),
        new AddressingMode(0b10, 0b000, 2, new RegFormulaPart("bx"), new RegFormulaPart("si"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b001, 2, new RegFormulaPart("bx"), new RegFormulaPart("di"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b010, 2, new RegFormulaPart("bp"), new RegFormulaPart("si"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b011, 2, new RegFormulaPart("bp"), new RegFormulaPart("di"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b100, 1, new RegFormulaPart("si"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b101, 1, new RegFormulaPart("di"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b110, 1, new RegFormulaPart("bp"), new OffsetFormulaPart(16)),
        new AddressingMode(0b10, 0b111, 1, new RegFormulaPart("bx"), new OffsetFormulaPart(16))
    };

    private ParsedPart parsePart(String val) {
        val = val.toLowerCase();
        for (String reg : registers) {
            if (reg.equalsIgnoreCase(val)) {
                return new ParsedPart(val, reg, 0, true);
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
            parts[i] = parts[i].trim();
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

                return new MemoryAddressing16Result(value, addressingMode.getMod(), addressingMode.getRm(), offset);
            }
        }

        return null;
    }
}
