package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Tay extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        alu.setY(alu.getA());
        return alu.getA();
    }

    @Override
    public boolean shouldSetNZ() {
        return true;
    }

}
