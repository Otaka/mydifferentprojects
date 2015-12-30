package com.nes.assembler;

import com.nes.processor.AluUtils;
import com.nes.processor.memory_addressing.AbstractMemoryAdressing;

/**
 * @author Dmitry
 */
class Instruction implements Comparable<Instruction> {

    private byte command;
    private AbstractMemoryAdressing ma;
    private String name;

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public AbstractMemoryAdressing getMa() {
        return ma;
    }

    public void setMa(AbstractMemoryAdressing ma) {
        this.ma = ma;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "0x" + AluUtils.byteToHexString(command) + ":" + name + ":" + ma.toString();
    }

    @Override
    public int compareTo(Instruction o) {
        return ma.getPriority()-o.getMa().getPriority();
    }
}
