package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Cpy extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        byte y = alu.getY();
        byte res = (byte) (y - input);
        if (AluUtils.unsignedByte(y) >= AluUtils.unsignedByte(input)) {
            alu.getAluFlags().setCarry(true);
        } else {
            alu.getAluFlags().setCarry(false);
        }
        return res;
    }

   
    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
