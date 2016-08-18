package com.asm.args.argresult;

import com.asm.Command;
import java.util.List;

/**
 * @author sad
 */
public class CommandFullParsingResult {

    private Command command;
    private List<AbstractParsingResult> parsingResult;

    public CommandFullParsingResult(Command command, List<AbstractParsingResult> parsingResult) {
        this.command = command;
        this.parsingResult = parsingResult;
    }

    public Command getCommand() {
        return command;
    }

    public List<AbstractParsingResult> getParsingResult() {
        return parsingResult;
    }
}
