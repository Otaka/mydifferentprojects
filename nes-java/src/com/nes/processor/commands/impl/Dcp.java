package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Dcp extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        int value = AluUtils.unsignedByte(input);
        value--;
        byte a = alu.getA();
        byte res = (byte) (a - value);
        if (AluUtils.unsignedByte(a) >= AluUtils.unsignedByte((byte) value)) {
            alu.getAluFlags().setCarry(true);
        } else {
            alu.getAluFlags().setCarry(false);
        }
        alu.getAluFlags().setZero(res == 0);
        alu.getAluFlags().setNegative((res & 0x80) != 0);
        return (byte) value;
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
