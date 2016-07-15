package com.nwn.data.script;

/**
 * @author Dmitry
 */
public class NwScript {
    private final byte[] commandArray;
    private final String name;

    public NwScript(byte[] commandArray, String name) {
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
