package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Lsr extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        int value = AluUtils.unsignedByte(input);
        alu.getAluFlags().setCarry(((value & 0x1) != 0));
        byte result = (byte) (value >> 1);
        return result;
    }

    

    @Override
    public StoreTo writeMemory() {
        return StoreTo.MememoryAdressing;
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
