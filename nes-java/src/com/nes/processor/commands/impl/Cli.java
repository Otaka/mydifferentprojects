package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Cli extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        alu.getAluFlags().setInterrupt(false);
        return 0;
    }
}
