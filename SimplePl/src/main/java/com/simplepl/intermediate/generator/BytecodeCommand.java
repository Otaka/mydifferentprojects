package com.simplepl.intermediate.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class BytecodeCommand {

    private short command;
    private BytecodeCommand parent;
    private List<BytecodeCommand> children = new ArrayList<>();
    private String dest;
    private String[] args;
    private BytecodeOperandSize operandSize;

    public void setOperandSize(BytecodeOperandSize operandSize) {
        this.operandSize = operandSize;
    }

    public BytecodeOperandSize getOperandSize() {
        return operandSize;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public String getDest() {
        return dest;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public short getCommand() {
        return command;
    }

    public List<BytecodeCommand> getChildren() {
        return children;
    }

    public BytecodeCommand getParent() {
        return parent;
    }

    public void setParent(BytecodeCommand parent) {
        this.parent = parent;
    }

}
