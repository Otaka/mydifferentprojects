package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Sta extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        return alu.getA();
    }

    @Override
    public StoreTo writeMemory() {
        return StoreTo.MememoryAdressing;
    }

}
