package com.asm;

/**
 * @author sad
 */
public class SplittedCommand {

    private String command;
    private String[] args;

    public int getArgumentsCount() {
        return args.length;
    }

    public SplittedCommand(String command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
