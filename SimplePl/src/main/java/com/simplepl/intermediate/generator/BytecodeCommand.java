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
    private IntermediateVar assignment;
    private IntermediateVar[] args;

    public void setCommand(short command) {
        this.command = command;
    }

    public void setAssignment(IntermediateVar assignment) {
        this.assignment = assignment;
    }

    public IntermediateVar getAssignment() {
        return assignment;
    }

    public void setArgs(IntermediateVar[] args) {
        this.args = args;
    }

    public IntermediateVar[] getArgs() {
        return args;
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
