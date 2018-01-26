package com.simplepl.intermediate.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class BytecodeGenerator {

    private List<BytecodeCommand> commands = new ArrayList<>();

    public void clear() {
        commands = new ArrayList<>();
    }

    public List<BytecodeCommand> getCommands() {
        return commands;
    }

    
}
