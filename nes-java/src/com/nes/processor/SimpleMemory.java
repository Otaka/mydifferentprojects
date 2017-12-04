package com.nes.processor;

import java.util.Arrays;

/**
 * @author Dmitry
 */
public class SimpleMemory extends Memory {
    byte[] memory = new byte[65536];

    @Override
    public void setValue(byte value, int address) {
       // if(address==0x4005){
       //     System.out.println("MEM = "+AluUtils.i16ToHexString(address)+" value "+AluUtils.byteToHexString(value));
       // }
        memory[address] = value;
    }

    @Override
    public byte getValue(int address) {
        return memory[address];
    }

    @Override
    public void printValues(int address, int size) {
        byte[]array=Arrays.copyOfRange(memory, address, address+size);
        System.out.println(AluUtils.bytesToHexString(array));
    }

}
