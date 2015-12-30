package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Bit extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        alu.getAluFlags().setOverflow((input & 0x40) != 0);
        alu.getAluFlags().setNegative((input & 0x80) != 0);
        byte result=(byte) (alu.getA() & input);
        alu.getAluFlags().setZero(result == 0);
        return result;
    }

    

    @Override
    public boolean shouldSetNZ() {
        return false;
    }
}
