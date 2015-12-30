package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Rla extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        int value = AluUtils.unsignedByte(input);
        boolean carry = alu.getAluFlags().isCarry();
        alu.getAluFlags().setCarry(((value & 0x80) != 0));
        value = value << 1;
        if (carry) {
            value = value | 1;
        }

        byte result=(byte) (value & 0xFF);
        byte a = alu.getA();
        byte res = (byte) (a & result);
        alu.setA(res);
        alu.setNZFlag(res);
        return result;
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
