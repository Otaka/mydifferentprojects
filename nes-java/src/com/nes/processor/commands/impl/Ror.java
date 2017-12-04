package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Ror extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        int value = AluUtils.unsignedByte(input);
        boolean carry = alu.getAluFlags().isCarry();
        alu.getAluFlags().setCarry(((value & 0x1) != 0));
        value = value >> 1;
        if (carry) {
            value = value | 0x80;
        }
        return (byte) value;
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
