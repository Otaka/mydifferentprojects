package com.asm;

import com.asm.args.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sad
 */
public class AssemblerSchemaParser {

    private Pattern parsePattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+\\[\\s*(.*?)\\s*\\]\\s+(.*)");
    private Map<String, List<Command>> availableCommands = new HashMap<>();
    private Context context;

    public AssemblerSchemaParser(Context context) {
        this.context = context;
    }

    public Map<String, List<Command>> loadSchema() {
        try (Scanner scanner = new Scanner(this.getClass().getResourceAsStream("insns.dat"))) {
            int lineIndex = 0;
            while (scanner.hasNextLine()) {
                try {
                    lineIndex++;
                    String line = scanner.nextLine();
                    line = line.trim();
                    if (line.startsWith(";") || line.isEmpty()) {
                        continue;
                    }

                    parseSchemaLine(line, lineIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return availableCommands;
    }

    private void parseSchemaLine(String line, int lineIndex) {
        Matcher matcher = parsePattern.matcher(line);
        if (!matcher.matches()) {
            throw new RuntimeException("Cannot load assembler library because of the malformed line №" + lineIndex + " \"" + line + "\"");
        }

        String mnem = matcher.group(1);
        String args = matcher.group(2);
        String argsDescription = matcher.group(3);
        String architecture = matcher.group(4);
        String[] descriptionArray = argsDescription.split("(\\:|\\||,|\\s+)");
        Set<String> descriptionSet = new HashSet<String>();
        Collections.addAll(descriptionSet, descriptionArray);
        int opcode = 0;
        try {
            opcode = findOpcode(descriptionArray);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Cannot find opcode in line №" + lineIndex + " \"" + line + "\"", ex);
        }

        Command command = new Command(mnem, opcode);
        command.setArchitectures(parseArchitectures(architecture));

        try {
            List<CommandArgument> arguments = parseCommandArguments(args, descriptionSet);
            command.setArgs(arguments.toArray(new CommandArgument[arguments.size()]));
        } catch (RuntimeException ex) {
            throw new RuntimeException("Exception while trying to parse arguemnts of the command in line №" + lineIndex + " \"" + line + "\"", ex);
        }

        List<Command> commandsArray = availableCommands.get(command.getMnemonic());
        if (commandsArray == null) {
            commandsArray = new ArrayList<>();
            availableCommands.put(command.getMnemonic(), commandsArray);
        }
        commandsArray.add(command);
    }

    protected List<CommandArgument> parseCommandArguments(String commandArgument, Set<String> argsDescriptions) {
        commandArgument = commandArgument.trim().toLowerCase();
        List<CommandArgument> arguments = new ArrayList<>();
        if (commandArgument.equals("void")) {
            return arguments;
        }
        String[] args = commandArgument.split(",");
        for (String arg : args) {
            arg = arg.trim();
            CommandArgument argument = null;
            if (arg.equals("imm") || arg.equals("imm8") || arg.equals("imm16") || arg.equals("imm32") || arg.equals("imm|near") || arg.equals("imm|far") || arg.equals("imm16|far") || arg.equals("imm16|near") || arg.equals("imm32|near") || arg.equals("imm32|far")) {
                argument = parseImmArgument(arg, argsDescriptions);
            }
            if (arg.equals("immsw")) {
                argument = new Immediate16CA(true);
            }
            if (arg.equals("imms8")) {
                argument = new Immediate8CA(true);
            }

            if (arg.equals("sbyteword")) {
                argument = new Immediate8CA(true);
            }
            if (arg.equals("sbytedword") || arg.equals("sbytedword32")) {
                argument = new Immediate32CA(true);
            }
            if (arg.equals("sbyteword16")) {
                argument = new Immediate16CA(true);
            }
            if (arg.equals("reg32na")) {
                argument = new RegisterCA().setEmbed(true);
            }
            if (arg.equals("mem_offs")) {
                argument = new MemoryOffsetCA();
            }

            if (arg.equals("mem") || arg.equals("mem32") || arg.equals("mem16")) {
                argument = parseMemArgument(argsDescriptions);
            }

            if (arg.equals("mem80")) {
                argument = parseMem80Argument(argsDescriptions);
            }

            if (arg.equals("mem64")) {
                argument = parseMem64Argument(argsDescriptions);
            }

            if (arg.equals("reg8") || arg.equals("reg16") || arg.equals("reg32")) {
                argument = parseRegArgument(arg, argsDescriptions);
            }

            if (arg.equals("reg_al")) {
                argument = new RegisterAlCA();
            }

            if (arg.equals("fpureg")) {
                if (argsDescriptions.contains("embreg")) {
                    argument = new RegisterFpuRegCA().setEmbed(true);
                } else {
                    argument = new RegisterFpuRegCA().setEmbed(false);
                }
            }
            if (arg.equals("fpu0")) {
                argument = new RegisterFpu0CA();
            }

            if (arg.equals("reg_ax")) {
                argument = new RegisterAxCA();
            }
            if (arg.equals("reg_dx")) {
                argument = new RegisterDxCA();
            }
            if (arg.equals("reg_eax")) {
                argument = new RegisterEaxCA();
            }
            if (arg.equals("reg_ecx")) {
                argument = new RegisterEcxCA();
            }
            if (arg.equals("reg_cx")) {
                argument = new RegisterCxCA();
            }
            if (arg.equals("reg_cl")) {
                argument = new RegisterClCA();
            }
            if (arg.equals("reg_es")) {
                argument = new RegisterEsCA();
            }
            if (arg.equals("reg_ds")) {
                argument = new RegisterDsCA();
            }
            if (arg.equals("reg_gs")) {
                argument = new RegisterGsCA();
            }
            if (arg.equals("reg_cs")) {
                argument = new RegisterCsCA();
            }
            if (arg.equals("reg_ss")) {
                argument = new RegisterSsCA();
            }
            if (arg.equals("reg_fs")) {
                argument = new RegisterFsCA();
            }
            if (arg.equals("reg_treg")) {
                argument = new RegisterTRegCA();
            }
            if (arg.equals("reg_dreg")) {
                argument = new RegisterDRegCA();
            }
            if (arg.equals("reg_sreg")) {
                argument = new RegisterSRegCA();
            }
            if (arg.equals("reg_creg")) {
                argument = new RegisterCRegCA();
            }
            if (arg.equals("unity")) {
                argument = parseUnityArgument(argsDescriptions);
            }
            if (arg.equals("rm8") || arg.equals("rm16") || arg.equals("rm32")) {
                argument = parseRMArgument(arg, argsDescriptions);
            }

            if (argument != null) {
                arguments.add(argument);
            } else {
                throw new RuntimeException("Command line argument \"" + arg + "\" is not implemented");
            }
        }

        return arguments;
    }

    private CommandArgument parseRMArgument(String arg, Set<String> argsDescriptions) {
        RMCA command;
        switch (arg) {
            case "rm8":
                command = new RM8CA();
                break;
            case "rm16":
                command = new RM16CA();
                break;
            case "rm32":
                command = new RM32CA();
                break;
            default:
                throw new RuntimeException("Cannot parse register/memory [" + arg + "]");
        }

        return command;
    }

    private CommandArgument parseRegArgument(String arg, Set<String> argsDescriptions) {
        RegisterCA command;
        switch (arg) {
            case "reg8":
                command = new Register8CA();
                break;
            case "reg16":
                command = new Register16CA();
                break;
            case "reg32":
                command = new Register32CA();
                break;
            default:
                throw new RuntimeException("Cannot parse register [" + arg + "]");
        }

        if (argsDescriptions.contains("embreg")) {
            command.setEmbed(true);
        }

        return command;
    }

    private CommandArgument parseImmArgument(String arg, Set<String> argsDescriptions) {
        if (argsDescriptions.contains("ib")) {
            return new Immediate8CA();
        }
        if (argsDescriptions.contains("iw")) {
            return new Immediate16CA();
        }
        if (argsDescriptions.contains("id")) {
            return new Immediate32CA();
        }

        if (argsDescriptions.contains("rel")) {
            if (context.getBits() == 16) {
                return new Immediate16CA(true);
            } else {
                return new Immediate32CA(true);
            }
        }

        if (argsDescriptions.contains("rel8")) {
            return new Immediate8CA(true);
        }

        throw new RuntimeException("cannot recognize immediate argument");
    }

    private CommandArgument parseMemArgument(Set<String> argsDescriptions) {
        if (argsDescriptions.contains("o16")) {
            return new Memory16CA();
        } else if (argsDescriptions.contains("o32")) {
            return new Memory32CA();
        }
        return new MemoryCA();
    }

    private CommandArgument parseUnityArgument(Set<String> argsDescriptions) {
        if (argsDescriptions.contains("o16")) {
            return new Immediate16CA(false);
        } else if (argsDescriptions.contains("o32")) {
            return new Immediate32CA(false);
        }
        return new Immediate8CA(false);
    }

    private CommandArgument parseMem80Argument(Set<String> argsDescriptions) {
        return new Memory80CA();
    }

    private CommandArgument parseMem64Argument(Set<String> argsDescriptions) {
        return new Memory64CA();
    }

    private Architecture[] parseArchitectures(String architectureLine) {
        String[] archStringArray = architectureLine.split(",");
        Architecture[] archArray = new Architecture[archStringArray.length];
        for (int i = 0; i < archArray.length; i++) {
            archArray[i] = Architecture.valueOf("ARCH_" + archStringArray[i].toUpperCase());
        }
        return archArray;
    }

    private int findOpcode(String[] argsDesription) {
        boolean found = false;
        int opCode = 0;
        for (String descr : argsDesription) {
            try {
                int value = Integer.parseInt(descr, 16);

                if (found) {//word opcode, not single byte opcode
                    opCode = (opCode & 0xFF) << 8;
                }
                opCode = opCode | (value & 0xFF);
                found = true;

            } catch (NumberFormatException ex) {
            }
        }

        if (found == false) {
            throw new RuntimeException("Cannot find opcode in line");
        }

        return opCode;
    }
}
