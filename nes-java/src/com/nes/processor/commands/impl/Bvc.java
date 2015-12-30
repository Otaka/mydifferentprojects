package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Bvc extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        if (alu.getAluFlags().isOverflow() == false) {
            alu.addToPc(input);
        }
        return 0;
    }

    
}
