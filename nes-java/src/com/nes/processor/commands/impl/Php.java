package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Php extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte status = alu.getAluFlags().getFlagByte();
        status=(byte)(status|0x10);
        alu.pushToStack(status);
        return 0;
    }

}
