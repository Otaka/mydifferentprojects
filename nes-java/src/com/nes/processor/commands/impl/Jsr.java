package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Jsr extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte l = alu.getNextByte();
        byte hi = alu.getNextByte();
        int pointer = AluUtils.lhTo16Bit(l, hi);//AluUtils.i16ToHexString(pointer);
        int index = alu.getPc();
        index--;
        byte high = (byte) (index & 0xff);
        byte low = (byte) ((index >> 8) & 0xff);

        alu.pushToStack(low);
        alu.pushToStack(high);
        alu.setPc(pointer);
        return 0;
    }

}
