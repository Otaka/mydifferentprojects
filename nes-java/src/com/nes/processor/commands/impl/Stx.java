package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Stx extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        return alu.getX();
    }

    @Override
    public StoreTo writeMemory() {
        return StoreTo.MememoryAdressing;
    }
}
