package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Ldx extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        alu.setX(input);
        return input;
    }

   

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
