package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Rts extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte hi = alu.popFromStack();
        byte low = alu.popFromStack();
        int index = AluUtils.lhTo16Bit(hi, low);//AluUtils.i16ToHexString(index);
        alu.setPc(index + 1);
        return 0;
    }
}
