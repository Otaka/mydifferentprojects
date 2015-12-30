package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * ADd with Carry
 */
public class And extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte a = alu.getA();
        byte res = (byte) (a & input);
        return res;
    }

    

    @Override
    public StoreTo writeMemory() {
        return StoreTo.Accumulator;
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }

}
