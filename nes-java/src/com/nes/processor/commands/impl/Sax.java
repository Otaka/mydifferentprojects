package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Sax extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte res = (byte) (alu.getA() & alu.getX());
        return res;
    }

    @Override
    public boolean shouldSetNZ() {
        return false;
    }

    @Override
    public StoreTo writeMemory() {
        return StoreTo.MememoryAdressing;
    }
}
