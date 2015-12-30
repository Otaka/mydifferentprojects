package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Adc extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte a = alu.getA();
        alu.setA(alu.add(a, input));
        return alu.getA();
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
