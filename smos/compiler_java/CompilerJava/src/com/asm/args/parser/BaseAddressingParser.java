package com.asm.args.parser;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author sad
 */
public abstract class BaseAddressingParser extends AbstractParser {

    protected final String[] registers = new String[]{"bx", "eax", "ebx", "ecx", "edx", "bp", "di", "ebp", "edi", "esi", "si", "eax*2", "ebx*2", "ecx*2", "edx*2", "ebp*2", "edi*2", "esi*2", "eax*4", "ebx*4", "ecx*4", "edx*4", "ebp*", "edi*4", "esi*4", "eax*8", "ebx*8", "ecx*8", "edx*8", "ebp*8", "edi*8", "esi*8"};

    /**
     sort parts in predefined order - first registers, then offsets. and registers should be sorted in alphabetic but grouped by reg type - first generic, then index, then offset
     */
    protected String[] sortAddressingParts(String[] parts) {
        Arrays.sort(parts, new Comparator<String>() {
            private int getRegIndex(String reg) {
                for (int i = 0; i < registers.length; i++) {
                    if (reg.equalsIgnoreCase(registers[i])) {
                        return i;
                    }
                }

                return 999;
            }

            @Override
            public int compare(String o1, String o2) {
                int first = getRegIndex(o1);
                int second = getRegIndex(o2);
                return first - second;
            }
        });

        return parts;
    }

    protected boolean isMatch(ParsedPart[] parts, AddressingMode addressingMode) {
        if (parts.length != addressingMode.getFormula().length) {
            return false;
        }

        for (int i = 0; i < parts.length; i++) {
            AbstractFormulaPart addrPart = addressingMode.getFormula()[i];
            ParsedPart part = parts[i];
            if (part.isReg()) {
                if (!addrPart.isReg()) {
                    return false;
                }

                RegFormulaPart regFormula = (RegFormulaPart) addrPart;
                if (!regFormula.getWord().equalsIgnoreCase(part.getReg())) {
                    return false;
                }
            } else {//if offset
                if (addrPart.isReg()) {
                    return false;
                }

                long offset = part.getOffset();
                NumberParser.MemSize size = NumberParser.MemSize.BYTE;
                OffsetFormulaPart offsetPart = (OffsetFormulaPart) addrPart;
                if (offsetPart.getBitsOffset() == 8) {
                    size = NumberParser.MemSize.BYTE;
                }
                if (offsetPart.getBitsOffset() == 16) {
                    size = NumberParser.MemSize.WORD;
                }
                if (offsetPart.getBitsOffset() == 32) {
                    size = NumberParser.MemSize.DWORD;
                }

                NumberParser parser = new NumberParser(size, Boolean.TRUE);
                if (!parser.checkLimits(offset)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected static class AddressingMode {

        private AbstractFormulaPart[] formula;
        private int mod;
        private int rm;
        private int offsetPosition;

        public AddressingMode(int mod, int rm, int offsetPosition, AbstractFormulaPart... parsedParts) {
            this.formula = parsedParts;
            this.mod = mod;
            this.rm = rm;
            this.offsetPosition = offsetPosition;
        }

        public AbstractFormulaPart[] getFormula() {
            return formula;
        }

        public int getMod() {
            return mod;
        }

        public int getRm() {
            return rm;
        }

        public int getOffsetPosition() {
            return offsetPosition;
        }

        public boolean hasOffset() {
            return offsetPosition != -1;
        }
    }

    protected static class AbstractFormulaPart {

        public boolean isReg() {
            return false;
        }
    }

    protected static class RegFormulaPart extends AbstractFormulaPart {

        private String word;

        public RegFormulaPart(String word) {
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        @Override
        public boolean isReg() {
            return true;
        }

        @Override
        public String toString() {
            return word;
        }

    }

    protected static class OffsetFormulaPart extends AbstractFormulaPart {

        private int bitsOffset;

        public OffsetFormulaPart(int bitsOffset) {
            this.bitsOffset = bitsOffset;
        }

        public int getBitsOffset() {
            return bitsOffset;
        }

        @Override
        public String toString() {
            return "offset" + bitsOffset;
        }

    }

    protected static class ParsedPart {

        private String originalPart;
        private String reg;
        private long offset;
        private boolean isReg;
        private boolean hasScale;

        public ParsedPart(String originalPart, String reg, long offset, boolean isReg) {
            this.originalPart = originalPart;
            this.reg = reg;
            this.offset = offset;
            this.isReg = isReg;
        }

        public ParsedPart setHasScale(boolean hasScale) {
            this.hasScale = hasScale;
            return this;
        }

        public boolean isHasScale() {
            return hasScale;
        }

        public String getOriginalPart() {
            return originalPart;
        }

        public long getOffset() {
            return offset;
        }

        public String getReg() {
            return reg;
        }

        public boolean isReg() {
            return isReg;
        }

        @Override
        public String toString() {
            if (isReg) {
                return reg;
            } else {
                return "offset: " + offset;
            }
        }
    }
}
