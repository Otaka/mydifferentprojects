package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class JmpAbsolute extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte l = alu.getNextByte();
        byte hi = alu.getNextByte();
        int pointer = AluUtils.lhTo16Bit(l, hi);
        alu.setPc(pointer);
        return 0;
    }

}
