package com.nes.assembler;

/**
 * @author Dmitry
 */
public class LabelToReplace {
    private final String label;
    private final int bytePosition;
    private final int size;
    private final int lineNumber;

    public LabelToReplace(String label, int bytePosition, int size, int lineNumber) {
        this.label = label;
        this.bytePosition = bytePosition;
        this.size = size;
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLabel() {
        return label;
    }

    public int getBytePosition() {
        return bytePosition;
    }

    public int getSize() {
        return size;
    }
}
