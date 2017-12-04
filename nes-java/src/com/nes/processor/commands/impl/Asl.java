package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Asl extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        int val = AluUtils.unsignedByte(input);
        alu.getAluFlags().setCarry(((val & 0x80) != 0));
        val = val << 1;
        AluUtils.byteToHexString((byte) val);
        return (byte) (val & 0xFF);
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
