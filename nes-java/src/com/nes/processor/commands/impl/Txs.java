package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class Txs extends AbstractCommand {
    @Override
    public byte execute(ALU alu, byte input) {
        alu.setS(alu.getX());
        return alu.getX();
    }

}
