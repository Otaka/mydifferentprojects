package com.nwn.script;

/**
 * @author Dmitry
 */
public class NwnScript {
    private final byte[] commandArray;
    private final String name;

    public NwnScript(byte[] commandArray, String name) {
        this.commandArray = commandArray;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte[] getCommandArray() {
        return commandArray;
    }

}
