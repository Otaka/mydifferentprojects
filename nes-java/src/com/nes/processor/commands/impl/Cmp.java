package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Cmp extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte a = alu.getA();
        byte res = (byte) (a - input);
        if (AluUtils.unsignedByte(a) >= AluUtils.unsignedByte(input)) {
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
