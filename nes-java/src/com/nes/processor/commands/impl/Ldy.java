package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Ldy extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        alu.setY(input);
        return input;
    }

  

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
