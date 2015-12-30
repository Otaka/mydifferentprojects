package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Bne extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        if (alu.getAluFlags().isZero() == false) {
            alu.addToPc(input);
        }
        return 0;
    }
}
