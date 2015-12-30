package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Pla extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte value=alu.popFromStack();
        return value;
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
