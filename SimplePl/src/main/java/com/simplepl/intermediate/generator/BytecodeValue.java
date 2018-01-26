package com.simplepl.intermediate.generator;

/**
 * @author sad
 */
public class BytecodeValue {

    private long value;

    public BytecodeValue(long value) {
        this.value = value;
    }

    public BytecodeValue() {
    }

    
    
    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

}
