package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Plp extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte status = alu.popFromStack();
        status=(byte)(status&0xef);
        alu.getAluFlags().setFlagByte(status);
        return 0;
    }

}
