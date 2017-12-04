package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Txa extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        alu.setA(alu.getX());
        return alu.getA();
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }
}
