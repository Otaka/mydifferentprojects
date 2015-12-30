package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Tsx extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        alu.setX((byte) alu.getS());
        return alu.getX();
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
