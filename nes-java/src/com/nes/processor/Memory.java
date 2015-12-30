package com.nes.processor;

/**
 * @author sad
 */
public abstract class Memory {

    public abstract byte getValue(int address);

    public abstract void setValue(byte value, int address);
    public void setValue(int value, int address){
        setValue((byte)(value&0xFF), address);
    }

    public void setBytes(byte[] bytes, int offset) {
        for (int i = 0; i < bytes.length; i++) {
            setValue(bytes[i], offset + i);
        }
    }
//Mostly for test purpose

    public void setBytes(int[] bytes, int offset) {
        for (int i = 0; i < bytes.length; i++) {
            setValue((byte) bytes[i], offset + i);
        }
    }
    
    public abstract void printValues(int address, int size);
}
