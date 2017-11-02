package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Rti extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte flag = alu.popFromStack();
        byte low = alu.popFromStack();
        byte hi = alu.popFromStack();
        int index = AluUtils.lhTo16Bit(low, hi);
        alu.setPc(index);
        alu.getAluFlags().setFlagByte(flag);
        return 0;
    }

}
