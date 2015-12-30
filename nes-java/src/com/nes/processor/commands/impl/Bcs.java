package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Bcs extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        if (alu.getAluFlags().isCarry() == true) {
            alu.addToPc(input);
        }
        return 0;
    }

   
}
