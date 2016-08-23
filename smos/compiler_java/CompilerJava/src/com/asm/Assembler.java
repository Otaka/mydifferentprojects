package com.asm;

import com.asm.args.argresult.AbstractParsingResult;
import com.asm.args.argresult.CommandFullParsingResult;
import com.asm.exceptions.ParsingException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sad
 */
public class Assembler {

    private Context context;
    private Map<String, List<Command>> availableCommands;
    private OutputStream outputStream;
    private Pattern assemblerPattern = Pattern.compile("(.*?)\\s+()");

    public Assembler(Context context, OutputStream outputStream) {
        this.context = context;
        AssemblerSchemaParser schemaParser = new AssemblerSchemaParser(context);
        availableCommands = schemaParser.loadSchema();
        this.outputStream = outputStream;
    }

    private void emmitByte(int byteValue) {
        try {
            outputStream.write(byteValue);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void emmitWord(int wordValue) {
        try {
            outputStream.write(wordValue & 0xff);
            outputStream.write((wordValue >> 8) & 0xff);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void emmitDWord(int dwordValue) {
        try {
            outputStream.write(dwordValue & 0xff);
            outputStream.write((dwordValue >> 8) & 0xff);
            outputStream.write((dwordValue >> 16) & 0xff);
            outputStream.write((dwordValue >> 24) & 0xff);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static int countSymbolInString(String assembleLine, char c) {
        int commaCount = 0;
        int count = assembleLine.length();
        for (int i = 0; i < count; i++) {
            if (assembleLine.charAt(i) == c) {
                commaCount++;
            }
        }
        return commaCount;
    }

    protected static SplittedCommand parseAssembleLine(String assembleLine) {
        assembleLine = StringUtils.strip(assembleLine);
        int spaceIndex = Math.max(assembleLine.indexOf(' '), assembleLine.indexOf('\t'));
        if (spaceIndex == -1) {
            return new SplittedCommand(assembleLine.toLowerCase(), new String[0]);
        }

        String command = assembleLine.substring(0, spaceIndex).toLowerCase();
        command = StringUtils.strip(command);
        if (command.isEmpty()) {
            throw new ParsingException("Command cannot be empty");
        }

        String argsLine = assembleLine.substring(spaceIndex);
        int commasCount = countSymbolInString(argsLine, ',');
        if (commasCount == 0) {
            return new SplittedCommand(command, new String[]{StringUtils.strip(argsLine)});
        } else {
            String[] splitted = argsLine.split(",");
            for (int i = 0; i < splitted.length; i++) {
                String arg = splitted[i];
                arg = StringUtils.strip(arg);
                if (arg.isEmpty()) {
                    throw new ParsingException("Argument cannot be empty string in the command '" + command + "'");
                }
                splitted[i] = arg;
            }

            if (splitted.length > 3) {
                throw new ParsingException("Command cannot have more than 3 arguments");
            }

            return new SplittedCommand(command, splitted);
        }
    }

    public void assemble(String assembleLine, int lineIndex) {
        SplittedCommand command;
        try {
            command = parseAssembleLine(assembleLine);
        } catch (ParsingException ex) {
            ex.setLine(lineIndex);
            throw ex;
        }

        List<Command> variants = availableCommands.get(command.getCommand().toUpperCase());
        if (variants == null) {
            throw new ParsingException("Do not recognize command [" + command.getCommand() + "] in line [" + assembleLine + "]", lineIndex);
        }
        CommandFullParsingResult result = matchCommands(command, variants);
        if (result == null) {
            throw new ParsingException("Cannot parse line [" + assembleLine + "]", lineIndex);
        }
    }

    public CommandFullParsingResult matchCommands(SplittedCommand stringCommand, List<Command> commands) {
        for (Command command : commands) {
            CommandFullParsingResult result = matchCommand(stringCommand, command);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private CommandFullParsingResult matchCommand(SplittedCommand stringCommand, Command command) {
        if (stringCommand.getArgumentsCount() != command.getArgumentsCount()) {
            return null;
        }

        List<AbstractParsingResult> argsParse = null;
        for (int i = 0; i < command.getArgumentsCount(); i++) {
            String arg = stringCommand.getArgs()[i];
            CommandArgument commandArg = command.getArgs()[i];
            AbstractParsingResult commandParsingResult = commandArg.match(arg);
            if (commandParsingResult == null) {
                return null;
            }

            if (argsParse == null) {
                argsParse = new ArrayList<>();
            }

            argsParse.add(commandParsingResult);
        }

        return new CommandFullParsingResult(command, argsParse);
    }

    public void assemble(String assembleLine) {
        assemble(assembleLine, -1);
    }
}
