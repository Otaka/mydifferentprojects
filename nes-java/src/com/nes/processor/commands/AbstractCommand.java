package com.nes.processor.commands;

import com.nes.processor.ALU;

/**
 * @author Dmitry
 */
public class AbstractCommand {

    public enum StoreTo {

        MememoryAdressing, Accumulator, None;
    };

    public byte execute(ALU alu, byte input) {
        return 0;
    }

    public StoreTo writeMemory() {
        return StoreTo.None;
    }

    public boolean shouldSetNZ() {
        return false;
    }
}
