package com.kotorresearch.script.assembler;

/**
 * @author Dmitry
 */
public enum OpcodeArgumentType {
    INT(4), SHORT(2), BYTE(1), STRING(-1), FLOAT(4), LABEL(4), FUNCTION_INDEX(2);
    private int size;

    private OpcodeArgumentType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
