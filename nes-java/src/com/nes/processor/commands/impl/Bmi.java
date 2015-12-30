package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Bmi extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        if (alu.getAluFlags().isNegative() == true) {
            alu.addToPc(input);
        }
        return 0;
    }

  
}
