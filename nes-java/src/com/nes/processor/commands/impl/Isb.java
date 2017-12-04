package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Isb extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        input = (byte)(AluUtils.unsignedByte(input)+1);
        byte a = alu.getA();
        alu.setA(alu.sub(a, input));
        alu.setNZFlag(alu.getA());
        return input;
    }

   

    @Override
    public StoreTo writeMemory() {
        return StoreTo.MememoryAdressing;
    }

    @Override
    public boolean shouldSetNZ() {
        return false;
    }
}
