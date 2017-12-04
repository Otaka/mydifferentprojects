package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Tya extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        alu.setA(alu.getY());
        return alu.getA();
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
