package com.nes.processor.commands;

import com.nes.processor.memory_addressing.AbstractMemoryAdressing;

/**
 * @author Dmitry
 */
public class CommandDefinition {

    private final byte code;
    private final AbstractCommand command;
    private final AbstractMemoryAdressing abstractMemoryAdressing;
    private final boolean unofficial;

    public CommandDefinition(byte code, AbstractCommand command, AbstractMemoryAdressing abstractMemoryAdressing, boolean unofficial) {
        this.code = code;
        this.command = command;
        this.abstractMemoryAdressing = abstractMemoryAdressing;
        this.unofficial = unofficial;
    }

    public boolean isUnofficial() {
        return unofficial;
    }

    public byte getCode() {
        return code;
    }

    public AbstractCommand getCommand() {
        return command;
    }

    public AbstractMemoryAdressing getAbstractMemoryAdressing() {
        return abstractMemoryAdressing;
    }
}
