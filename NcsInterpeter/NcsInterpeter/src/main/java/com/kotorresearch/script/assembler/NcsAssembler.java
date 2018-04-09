package com.kotorresearch.script.assembler;

import com.kotorresearch.script.utils.ByteArrayUtils;
import com.kotorresearch.script.data.Function;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.input.BOMInputStream;

/**
 * @author Dmitry
 */
public class NcsAssembler {

    private static final String MAGIC_SEQUENCE = "NCS V1.0";
    private static final String COMMENT_START = "/*";
    private static final String COMMENT_END = "*/";
    private Map<String, RelocationLabel> relocations = new HashMap<>();
    private Map<String, Label> labels = new HashMap<>();
    private DecimalFormat floatParser;
    private static Map<Integer, Function> functionsMap;
    public static OpcodeHandler[] opcodeHandlers = new OpcodeHandler[]{
        new OpcodeHandler("CPDOWNSP\\s+(.+)\\s*,\\s*(.+)", 0x01, 0x1, true).setArgumentTypes(OpcodeArgumentType.INT, OpcodeArgumentType.SHORT),
        new OpcodeHandler("RSADDI", 0x02, 0x3, true),
        new OpcodeHandler("RSADDF", 0x02, 0x4, true),
        new OpcodeHandler("RSADDS", 0x02, 0x5, true),
        new OpcodeHandler("RSADDO", 0x02, 0x6, true),
        new OpcodeHandler("RSADDEFF", 0x02, 0x10, true),//effect
        new OpcodeHandler("RSADDLOC", 0x02, 0x12, true),//location
        new OpcodeHandler("RSADDTAL", 0x02, 0x13, true),//talent
        new OpcodeHandler("CPTOPSP\\s+(.+)\\s*,\\s*(.+)", 0x03, 0x1, true).setArgumentTypes(OpcodeArgumentType.INT, OpcodeArgumentType.SHORT),
        new OpcodeHandler("CONSTI\\s+(.+)", 0x04, 0x3, true).setArgumentTypes(OpcodeArgumentType.INT),
        new OpcodeHandler("CONSTF\\s+(.+)", 0x04, 0x4, true).setArgumentTypes(OpcodeArgumentType.FLOAT),
        new OpcodeHandler("CONSTS\\s+\\\"(.+?)\\\"", 0x04, 0x5, true, new CONSTS()).setArgumentTypes(OpcodeArgumentType.STRING),
        new OpcodeHandler("CONSTO\\s+(.+)", 0x04, 0x6, true).setArgumentTypes(OpcodeArgumentType.INT),
        new OpcodeHandler("ACTION\\s+(.+)\\s*,\\s*(.+)", 0x05, 0x0, true, new ActionLineAssembler()).setArgumentTypes(OpcodeArgumentType.FUNCTION_INDEX, OpcodeArgumentType.BYTE),
        new OpcodeHandler("LOGANDII", 0x06, 0x20, true),
        new OpcodeHandler("LOGORII", 0x07, 0x20, true),
        new OpcodeHandler("INCORII", 0x08, 0x20, true),
        new OpcodeHandler("EXCORII", 0x09, 0x20, true),////////////////////////////////////////////
        new OpcodeHandler("BOOLANDII", 0x0A, 0x20, true),///////////////////////////////////////////
        new OpcodeHandler("EQUALII", 0x0B, 0x20, true),
        new OpcodeHandler("EQUALFF", 0x0B, 0x21, true),
        new OpcodeHandler("EQUALOO", 0x0B, 0x22, true),
        new OpcodeHandler("EQUALSS", 0x0B, 0x23, true),
        new OpcodeHandler("EQUALTT", 0x0B, 0x24, true).setArgumentTypes(OpcodeArgumentType.SHORT),////////////////////////////////////////////////
        new OpcodeHandler("NEQUALII", 0x0C, 0x20, true),
        new OpcodeHandler("NEQUALFF", 0x0C, 0x21, true),////////////////////////////////////////////////
        new OpcodeHandler("NEQUALOO", 0x0C, 0x22, true),
        new OpcodeHandler("NEQUALSS", 0x0C, 0x23, true),
        new OpcodeHandler("NEQUALTT", 0x0C, 0x24, true).setArgumentTypes(OpcodeArgumentType.SHORT),/////////////////////////////////////////////////
        new OpcodeHandler("GEQII", 0x0D, 0x20, true),
        new OpcodeHandler("GEQFF", 0x0D, 0x21, true),
        new OpcodeHandler("GTII", 0x0E, 0x20, true),
        new OpcodeHandler("GTFF", 0x0E, 0x21, true),
        new OpcodeHandler("LTII", 0x0F, 0x20, true),
        new OpcodeHandler("LTFF", 0x0F, 0x21, true),
        new OpcodeHandler("LEQII", 0x10, 0x20, true),
        new OpcodeHandler("LEQFF", 0x10, 0x21, true),
        new OpcodeHandler("SHLEFTII", 0x11, 0x20, true),
        new OpcodeHandler("SHRIGHTII", 0x12, 0x20, true),//////////////////////////////////////////////////
        new OpcodeHandler("USHRIGHTII", 0x13, 0x20, true),/////////////////////////////////////////////////
        new OpcodeHandler("ADDII", 0x14, 0x20, true),
        new OpcodeHandler("ADDIF", 0x14, 0x25, true),//////////////////////////////////////////////////////
        new OpcodeHandler("ADDFI", 0x14, 0x26, true),//////////////////////////////////////////////////////
        new OpcodeHandler("ADDFF", 0x14, 0x21, true),
        new OpcodeHandler("ADDSS", 0x14, 0x23, true),
        new OpcodeHandler("ADDVV", 0x14, 0x3A, true),/////////////////////////////////////////////////////
        new OpcodeHandler("SUBII", 0x15, 0x20, true),
        new OpcodeHandler("SUBIF", 0x15, 0x25, true),//////////////////////////////////////////////////////
        new OpcodeHandler("SUBFI", 0x15, 0x26, true),//////////////////////////////////////////////////////
        new OpcodeHandler("SUBFF", 0x15, 0x21, true),
        new OpcodeHandler("SUBVV", 0x15, 0x3A, true),//////////////////////////////////////////////////////
        new OpcodeHandler("MULII", 0x16, 0x20, true),
        new OpcodeHandler("MULIF", 0x16, 0x25, true),/////////////////////////////////////////////////////
        new OpcodeHandler("MULFI", 0x16, 0x26, true),/////////////////////////////////////////////////////
        new OpcodeHandler("MULFF", 0x16, 0x21, true),
        new OpcodeHandler("MULVF", 0x16, 0x3B, true),//////////////////////////////////////////////////////
        new OpcodeHandler("MULFV", 0x16, 0x3C, true),//////////////////////////////////////////////////////
        new OpcodeHandler("DIVII", 0x17, 0x20, true),
        new OpcodeHandler("DIVIF", 0x17, 0x25, true),//////////////////////////////////////////////////////
        new OpcodeHandler("DIVFI", 0x17, 0x26, true),//////////////////////////////////////////////////////
        new OpcodeHandler("DIVFF", 0x17, 0x21, true),
        new OpcodeHandler("DIVVF", 0x17, 0x3B, true),//////////////////////////////////////////////////////
        new OpcodeHandler("MODII", 0x18, 0x20, true),
        new OpcodeHandler("NEGI", 0x19, 0x03, true),
        new OpcodeHandler("NEGF", 0x19, 0x04, true),
        new OpcodeHandler("COMPI", 0x1A, 0x03, true),//////////////////////////////////////////////////////
        new OpcodeHandler("MOVSP\\s+(.+)", 0x1B, 0x0, true).setArgumentTypes(OpcodeArgumentType.INT),
        new OpcodeHandler("JMP\\s+(.+)", 0x1D, 0x0, true).setArgumentTypes(OpcodeArgumentType.LABEL).setHasRelocation(true),
        new OpcodeHandler("JSR\\s+(.+)", 0x1E, 0x0, true).setArgumentTypes(OpcodeArgumentType.LABEL).setHasRelocation(true),
        new OpcodeHandler("JZ\\s+(.+)", 0x1F, 0x0, true).setArgumentTypes(OpcodeArgumentType.LABEL).setHasRelocation(true),
        new OpcodeHandler("RETN", 0x20, 0x0, true),
        new OpcodeHandler("DESTRUCT", 0x21, 0x1, true).setArgumentTypes(OpcodeArgumentType.SHORT, OpcodeArgumentType.SHORT, OpcodeArgumentType.SHORT),
        new OpcodeHandler("NOTI", 0x22, 0x03, true),
        new OpcodeHandler("DECISP\\s+(.+)", 0x23, 0x03, true).setArgumentTypes(OpcodeArgumentType.INT),
        new OpcodeHandler("INCISP\\s+(.+)", 0x24, 0x03, true).setArgumentTypes(OpcodeArgumentType.INT),
        new OpcodeHandler("JNZ\\s+(.+)", 0x25, 0x0, true).setArgumentTypes(OpcodeArgumentType.LABEL).setHasRelocation(true),
        new OpcodeHandler("CPDOWNBP\\s+(.+)\\s*,\\s*(.+)", 0x26, 0x1, true).setArgumentTypes(OpcodeArgumentType.INT, OpcodeArgumentType.SHORT),
        new OpcodeHandler("CPTOPBP\\s+(.+)\\s*,\\s*(.+)", 0x27, 0x1, true).setArgumentTypes(OpcodeArgumentType.INT, OpcodeArgumentType.SHORT),
        new OpcodeHandler("DECIBP\\s+(.+)", 0x28, 0x03, true).setArgumentTypes(OpcodeArgumentType.INT),////////////////////////////////////////////////
        new OpcodeHandler("INCIBP\\s+(.+)", 0x29, 0x03, true).setArgumentTypes(OpcodeArgumentType.INT),////////////////////////////////////////////////
        new OpcodeHandler("SAVEBP", 0x2A, 0x00, true),
        new OpcodeHandler("RESTOREBP", 0x2B, 0x00, true),
        new OpcodeHandler("STORE_STATE\\s+(.+)\\s*,\\s*(.+)", 0x2C, 0x10, true).setArgumentTypes(OpcodeArgumentType.INT, OpcodeArgumentType.INT),
        new OpcodeHandler("NOP", 0x2D, 0x00, true),
        new OpcodeHandler("T\\s+(.+)", 0x42, 0x00, false).setArgumentTypes(OpcodeArgumentType.INT)
    };

    public NcsAssembler() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        floatParser = new DecimalFormat("0.#");
        floatParser.setDecimalFormatSymbols(symbols);
    }

    public byte[] assemble(String value, boolean writeHeader) {
        relocations.clear();
        labels.clear();
        value = removeComments(value);
        ByteArrayOutputStream output = new ByteArrayOutputStream(1000);
        String[] lines = value.split("[\\r\\n]+");
        int lineNumber = -1;
        for (String line : lines) {
            lineNumber++;
            processLine(line, output, lineNumber);
        }

        byte[] bytes = output.toByteArray();
        processRelocations(bytes);
        if (writeHeader) {
            byte[] newArray = new byte[bytes.length + 8];
            System.arraycopy(bytes, 0, newArray, 8, bytes.length);
            byte[] headerBytes = MAGIC_SEQUENCE.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(headerBytes, 0, newArray, 0, headerBytes.length);
            bytes = newArray;
        }

        return bytes;
    }

    private void processRelocations(byte[] dumpedByteBuffer) {
        for (String relocationLabel : relocations.keySet()) {
            RelocationLabel relocationLabelObject = relocations.get(relocationLabel);

            Label label = labels.get(relocationLabel);
            if (label == null) {
                throw new IllegalArgumentException("Cannot find label [" + relocationLabel + "] defined in line [" + relocationLabelObject.getLineNumber() + "]");
            }

            int addressArgumentOffset = relocationLabelObject.getCommandStart() + relocationLabelObject.getArgumentOffsetFromCommandStart();
            int address = label.getOffset() - relocationLabelObject.getCommandStart();
            ByteArrayUtils.putIntToByteBuffer(dumpedByteBuffer, address, addressArgumentOffset);
        }
    }

    private void addLabel(String labelString, ByteArrayOutputStream output, int lineNumber) {
        if (labels.containsKey(labelString)) {
            Label label = labels.get(labelString);
            throw new IllegalArgumentException("Line [" + lineNumber + "] contains label [" + labelString + "] that is already defined in line [" + label.getLineNumber() + "]");
        }

        int currentOffset = output.size();
        Label label = new Label(labelString, currentOffset, lineNumber);
        labels.put(labelString, label);
    }

    private void processLine(String line, ByteArrayOutputStream output, int lineNumber) {
        line = line.trim();
        if (line.isEmpty()) {
            return;
        }

        LineAndLabel ll = splitLineOnLabelAndDataLine(line);
        if (ll.label != null) {
            addLabel(ll.label, output, lineNumber);
        }
        line = ll.line;

        for (OpcodeHandler opcodeHandler : opcodeHandlers) {
            Matcher matcher = opcodeHandler.getPattern().matcher(line);
            if (matcher.matches()) {
                int startOfCommandOffset = output.size();
                if (opcodeHandler.getCustomAssembler() != null) {
                    opcodeHandler.customAssembler.assembleLine(opcodeHandler, line, output, startOfCommandOffset);
                } else {
                    output.write(opcodeHandler.getCode());
                    if (opcodeHandler.isHasType()) {
                        output.write(opcodeHandler.getType());
                    }

                    if (opcodeHandler.getArguments() != null) {
                        for (int i = 0; i < opcodeHandler.getArguments().length; i++) {
                            OpcodeArgumentType argument = opcodeHandler.getArguments()[i];
                            String value = matcher.group(i + 1);
                            switch (argument) {
                                case BYTE:
                                    int byteValue = Integer.parseInt(value, 16);
                                    output.write(byteValue);
                                    break;
                                case FLOAT:
                                    float floatValue;
                                    try {
                                        floatValue = floatParser.parse(value).floatValue();
                                    } catch (ParseException ex) {
                                        throw new IllegalArgumentException("" + lineNumber + ": Cannot parse value [" + value + "] as float");
                                    }
                                    writeIntToOutputStream(output, Float.floatToIntBits(floatValue));
                                    break;
                                case INT:
                                    writeIntToOutputStream(output, parseInt(value, 16));
                                    break;
                                case SHORT:
                                case FUNCTION_INDEX:
                                    writeShortToOutputStream(output, Integer.parseInt(value, 16));
                                    break;
                                case STRING:
                                    writeShortToOutputStream(output, value.length());
                                    try {
                                        output.write(value.getBytes(StandardCharsets.UTF_8));
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;
                                case LABEL:
                                    String label = value;
                                    relocations.put(label, new RelocationLabel(label, startOfCommandOffset, output.size() - startOfCommandOffset, lineNumber));
                                    writeIntToOutputStream(output, -1);//some temp value
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                return;
            }
        }
        throw new IllegalArgumentException("Cannot parse the line [" + line + "]");
    }

    public static int parseInt(String s, int radix)
            throws NumberFormatException {
        /*
         * WARNING: This method may be invoked early during VM initialization
         * before IntegerCache is initialized. Care must be taken to not use
         * the valueOf method.
         */

        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < Character.MIN_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " less than Character.MIN_RADIX");
        }

        if (radix > Character.MAX_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " greater than Character.MAX_RADIX");
        }

        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw new RuntimeException("Cannot parse number [" + s + "]");
                }

                if (len == 1) // Cannot have lone "+" or "-"
                {
                    throw new RuntimeException("Cannot parse number [" + s + "]");
                }
                i++;
            }
            multmin = limit;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    throw new RuntimeException("Cannot parse number [" + s + "]");
                }
                if (result < multmin) {
                    throw new RuntimeException("Cannot parse number [" + s + "]");
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new RuntimeException("Cannot parse number [" + s + "]");
                }
                result -= digit;
            }
        } else {
            throw new RuntimeException("Cannot parse number [" + s + "]");
        }

        return negative ? result : -result;
    }

    private void writeIntToOutputStream(OutputStream stream, int value) {
        byte[] array = new byte[4];
        ByteArrayUtils.putIntToByteBuffer(array, value, 0);
        try {
            stream.write(array);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void writeShortToOutputStream(OutputStream stream, int value) {
        byte[] array = new byte[2];
        ByteArrayUtils.putShortToByteBuffer(array, value, 0);
        try {
            stream.write(array);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected LineAndLabel splitLineOnLabelAndDataLine(String line) {
        Pattern pattern = Pattern.compile("((?<label>[^\\s]+)\\s*:\\s*)?(?<dataline>.*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            LineAndLabel ll = new LineAndLabel();
            ll.label = trim(matcher.group("label"));
            ll.line = trim(matcher.group("dataline"));
            return ll;
        }

        throw new RuntimeException("For some reason could not split line [" + line + "] on label:dataline. Error in app logic");
    }

    private String trim(String line) {
        if (line == null) {
            return null;
        }
        return line.trim();
    }

    protected String removeComments(String line) {
        char[] characters = line.toCharArray();

        while (true) {
            int commentStartIndex = line.indexOf(COMMENT_START);
            if (commentStartIndex == -1) {//there is no comment
                return String.copyValueOf(characters);
            }

            int commentEndIndex = line.indexOf(COMMENT_END);
            if (commentEndIndex == -1) {//there is no comment
                return String.copyValueOf(characters);
            }

            if (commentEndIndex < commentStartIndex) {
                throw new IllegalArgumentException("Found that end of comment '*/' appeared before start of comment '/*'");
            }

            int endIndex = commentEndIndex + COMMENT_END.length();
            for (int i = commentStartIndex; i < endIndex; i++) {
                char c = characters[i];
                if (c != '\n') {
                    characters[i] = ' ';
                }
            }

            line = String.copyValueOf(characters);
        }
    }

    public List<DisassembledLine> disassembler(byte[] buffer) {
        //remove magic sequence if it is present
        byte[] magicSequenceByteBuffer = MAGIC_SEQUENCE.getBytes(StandardCharsets.UTF_8);
        int currentIp = 0;
        if (ByteArrayUtils.arrayContainsArray(buffer, 0, magicSequenceByteBuffer)) {
            currentIp = magicSequenceByteBuffer.length;
        }

        //do disassembling;
        List<DisassembledLine> disassembledLines = new ArrayList<>();

        while (true) {
            if (currentIp >= buffer.length) {
                break;
            }

            DisassembledLine disassembledLine = disassembleNextOpcode(currentIp, buffer);
            currentIp += disassembledLine.getBytesLength();
            disassembledLines.add(disassembledLine);
        }
        return disassembledLines;
    }

    private DisassembledLine disassembleNextOpcode(int currentIp, byte[] buffer) {
        OpcodeHandler opcodeHandler = findOpcodeHandler(currentIp, buffer);
        opcodeHandler.setUsed(true);
        DisassembledLine disassembledLine;
        Map<String, Object> attributes = new HashMap<>();
        int startOfCommandAddress = currentIp;
        int length = 1;
        String pattern = opcodeHandler.getPattern().pattern();
        String opcodeString = cutStringBeforeSymbol(pattern, "\\");
        attributes.put("opcode", opcodeString);
        StringBuilder sb = new StringBuilder();
        sb.append(opcodeString);
        if (opcodeHandler.isHasType()) {
            length++;
        }

        if (opcodeHandler.getArguments() != null && opcodeHandler.getArguments().length > 0) {
            sb.append(" ");
            boolean first = true;
            for (OpcodeArgumentType argument : opcodeHandler.getArguments()) {
                if (first == false) {
                    sb.append(",");
                } else {
                    first = false;
                }

                if (null != argument) {
                    switch (argument) {
                        case BYTE:
                            sb.append(ByteArrayUtils.hex(ByteArrayUtils.getByteFromByteBuffer(buffer, startOfCommandAddress + length) & 0xFF, argument.getSize()));
                            length += argument.getSize();
                            break;
                        case FLOAT: {
                            int value = ByteArrayUtils.getIntFromByteBuffer(buffer, startOfCommandAddress + length);
                            sb.append(Float.intBitsToFloat(value));
                            length += 4;
                            break;
                        }
                        case INT: {
                            int value = ByteArrayUtils.getIntFromByteBuffer(buffer, startOfCommandAddress + length);
                            sb.append(ByteArrayUtils.hex(value, argument.getSize()));
                            length += argument.getSize();
                            break;
                        }
                        case SHORT: {
                            int value = ByteArrayUtils.getShortFromByteBuffer(buffer, startOfCommandAddress + length) & 0xFFFF;
                            sb.append(ByteArrayUtils.hex(value, argument.getSize()));
                            length += argument.getSize();
                            break;
                        }
                        case FUNCTION_INDEX: {

                            int value = ByteArrayUtils.getShortFromByteBuffer(buffer, startOfCommandAddress + length) & 0xFFFF;
                            Function function = getFunctionsMap().get(value);
                            String functionName;
                            if (function == null) {
                                functionName = "UNKNOWN_FUNCTION";
                            } else {
                                functionName = function.getFunctionName();
                            }
                            attributes.put("functionIndex", value);
                            attributes.put("functionName", functionName);
                            sb.append(functionName).append("(").append(ByteArrayUtils.hex(value, argument.getSize())).append(")");
                            length += argument.getSize();
                            break;
                        }
                        case STRING:
                            int stringSize = ByteArrayUtils.getShortFromByteBuffer(buffer, startOfCommandAddress + length) & 0xFFFF;
                            length += 2;
                            String str = ByteArrayUtils.getStringFromByteBuffer(buffer, startOfCommandAddress + length, stringSize);
                            sb.append("\"").append(str).append("\"");
                            length += stringSize;
                            break;
                        case LABEL:
                            int jumpTo = ByteArrayUtils.getIntFromByteBuffer(buffer, startOfCommandAddress + length);
                            length += argument.getSize();
                            sb.append(ByteArrayUtils.hex(jumpTo + startOfCommandAddress, argument.getSize()));
                            break;
                        default:
                            throw new IllegalArgumentException("Disassembling of argument [" + argument + "] is not implemented");
                    }
                }
            }

        }

        disassembledLine = new DisassembledLine(sb.toString(), startOfCommandAddress, length);
        disassembledLine.setAttributes(attributes);
        currentIp = startOfCommandAddress;
        StringBuilder byteArrayDumpStringBuilder = new StringBuilder();
        byteArrayDumpStringBuilder.append(ByteArrayUtils.hex(ByteArrayUtils.getByteFromByteBuffer(buffer, currentIp), 1));
        currentIp++;
        if (opcodeHandler.isHasType()) {
            byteArrayDumpStringBuilder.append(" ");
            byteArrayDumpStringBuilder.append(ByteArrayUtils.hex(ByteArrayUtils.getByteFromByteBuffer(buffer, currentIp), 1));
            currentIp++;
        }

        if (opcodeHandler.getArguments() != null && opcodeHandler.getArguments().length > 0) {
            byteArrayDumpStringBuilder.append(" ");
            boolean first = true;
            for (OpcodeArgumentType argument : opcodeHandler.getArguments()) {
                if (first == false) {
                    byteArrayDumpStringBuilder.append(" ");
                } else {
                    first = false;
                }

                if (argument == OpcodeArgumentType.STRING) {
                    byteArrayDumpStringBuilder.append(ByteArrayUtils.printBuffer(buffer, currentIp, OpcodeArgumentType.SHORT.getSize(), first));
                    int size = ByteArrayUtils.getShortFromByteBuffer(buffer, currentIp);
                    currentIp += 2;
                    byteArrayDumpStringBuilder.append(" ");
                    byteArrayDumpStringBuilder.append("str");
                    currentIp += size;
                } else {
                    byteArrayDumpStringBuilder.append(ByteArrayUtils.printBuffer(buffer, currentIp, argument.getSize(), false));
                    currentIp += argument.getSize();

                }
            }
        }

        disassembledLine.setByteDumpString(byteArrayDumpStringBuilder.toString());

        return disassembledLine;
    }

    private String cutStringBeforeSymbol(String value, String symbol) {
        int position = value.indexOf(symbol);
        if (position == -1) {
            return value;
        }
        return value.substring(0, position);
    }

    private OpcodeHandler findOpcodeHandler(int currentIp, byte[] buffer) {
        int opcodeValue = buffer[currentIp];
        for (OpcodeHandler opcodeHandler : opcodeHandlers) {
            if (opcodeHandler.getCode() == opcodeValue) {
                if (opcodeHandler.isHasType()) {
                    int type = buffer[currentIp + 1];
                    if (type == opcodeHandler.getType()) {
                        return opcodeHandler;
                    }
                } else {
                    return opcodeHandler;
                }
            }
        }

        throw new IllegalArgumentException("Cannot decode instruction [" + ByteArrayUtils.hex(opcodeValue, 1) + "] at offset [" + ByteArrayUtils.hex(currentIp, 4) + "]");
    }

    private static Map<Integer, Function> getFunctionsMap() {
        if (functionsMap == null) {
            loadFunctionsList("/functionsList.txt");
        }
        return functionsMap;
    }

    private static void loadFunctionsList(String pathToResourceFile) {
        Scanner scanner = new Scanner(new BOMInputStream(NcsAssembler.class.getResourceAsStream(pathToResourceFile)));
        functionsMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\t");
            int index = Integer.parseInt(parts[0]);
            String returnType = parts[1];
            String name = parts[2];
            int argsCount = parts.length - 3 - 1;
            String usedInKotorString = parts[parts.length - 1];
            boolean usedInKotor;
            if (usedInKotorString.equals("used")) {
                usedInKotor = true;
            } else if (usedInKotorString.equals("not_used")) {
                usedInKotor = false;
            } else {
                throw new IllegalArgumentException("Latest column should be 'used in kotor' and kontains only [used, not_used], but it contains [" + usedInKotorString + "]");
            }

            Function f = new Function(name, index, returnType);
            f.setUsedInKotor(usedInKotor);
            for (int i = 0; i < argsCount; i++) {
                f.getArguments().add(parts[i + 3]);
            }
            functionsMap.put(index, f);
        }
    }

    public static class DisassembledLine {

        private String line;
        private int offset;
        private int byteLength;
        private String label;
        private String byteDumpString;

        private Map<String, Object> attributes;

        public DisassembledLine(String line, int offset, int byteLength) {
            this.line = line;
            this.offset = offset;
            this.byteLength = byteLength;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setByteDumpString(String byteDumpString) {
            this.byteDumpString = byteDumpString;
        }

        public void setByteLength(int byteLength) {
            this.byteLength = byteLength;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getLabel() {
            return label;
        }

        public String getByteDumpString() {
            return byteDumpString;
        }

        public int getBytesLength() {
            return byteLength;
        }

        public String getLine() {
            return line;
        }

        public int getOffset() {
            return offset;
        }
    }

    private static class CONSTS implements CustomLineAssembler {

        @Override
        public void assembleLine(OpcodeHandler assembleLine, String line, ByteArrayOutputStream output, int startOfCommandOffset) {
            try {
                Matcher matcher = assembleLine.pattern.matcher(line);
                matcher.matches();
                String value = matcher.group(1);
                output.write(assembleLine.code);
                output.write(assembleLine.type);
                byte[] buffer = new byte[4];
                ByteArrayUtils.putShortToByteBuffer(buffer, value.length(), 0);
                output.write(buffer, 0, 2);
                output.write(value.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * functionString can have several representations:<br>
     * 34 - just index of function<br>
     * functionName - name of the function<br>
     * functionName(functionIndex) - name of the function with duplicated
     * function index. In case if function name is not found, <br>
     * method will just write warning, and will assemble command with
     * functionIndex
     */
    private static int findFunctionIndex(String functionString, int argsCount) {
        functionString = functionString.trim();
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher matcher = numberPattern.matcher(functionString);
        if (matcher.matches()) {
            return Integer.parseInt(functionString, 16);
        }

        Pattern functionNameWithIndex = Pattern.compile("([a-zA-Z0-9_]+)\\s*\\((\\s*\\d+)\\s*\\)");
        matcher = functionNameWithIndex.matcher(functionString);
        if (matcher.matches()) {
            String functionName = matcher.group(1);
            int functionIndex = Integer.parseInt(matcher.group(2), 16);
            Function function = findFunctionByName(functionName);
            if (function == null) {
                System.out.println("WARN. Function [" + functionName + "] is not found. Used [" + functionIndex + "] that is provided");
            } else if (function.getFunctionIndex() != functionIndex) {
                System.out.println("WARN. Function [" + functionName + "] has index [" + function.getFunctionIndex() + "], but you have provided index [" + functionIndex + "]. Assembled using your functionIndex");
                if (function.getArguments().size() != argsCount) {
                    System.out.println("WARN. Function [" + functionName + "] has [" + function.getArguments().size() + "] arguments, but you have provided [" + argsCount + "]");
                }
            }

            return functionIndex;
        }

        Pattern functionNamePattern = Pattern.compile("([a-zA-Z0-9_]+)");
        matcher = functionNamePattern.matcher(functionString);
        if (matcher.matches()) {
            String functionName = matcher.group(1);
            Function function = findFunctionByName(functionName);
            if (function == null) {
                throw new IllegalArgumentException("Unknown function [" + functionName + "].");
            }

            if (function.getArguments().size() != argsCount) {
                System.out.println("WARN. Function [" + functionName + "] has [" + function.getArguments().size() + "] arguments, but you have provided only [" + argsCount + "]");
            }

            return function.getFunctionIndex();
        }

        throw new IllegalArgumentException("Cannot parse function name in ACTION command [" + functionString + "]");
    }

    private static Function findFunctionByName(String functionName) {
        for (Function function : getFunctionsMap().values()) {
            if (function.getFunctionName().equals(functionName)) {
                return function;
            }
        }
        throw new IllegalArgumentException("Cannot find function [" + functionName + "]");
    }

    private static class ActionLineAssembler implements CustomLineAssembler {

        @Override
        public void assembleLine(OpcodeHandler assembleLine, String line, ByteArrayOutputStream output, int startOfCommandOffset) {
            try {
                Matcher matcher = assembleLine.pattern.matcher(line);
                matcher.matches();

                String functionName = matcher.group(1);
                int argsCount = Integer.parseInt(matcher.group(2));
                int functionIndex = findFunctionIndex(functionName, argsCount);
                output.write(assembleLine.code);
                output.write(assembleLine.type);
                byte[] buffer = new byte[2];
                ByteArrayUtils.putShortToByteBuffer(buffer, functionIndex, 0);
                output.write(buffer);
                buffer[0] = (byte) argsCount;
                output.write(buffer, 0, 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class Label {

        private String label;
        private int offset;
        private int lineNumber;

        public Label(String label, int offset, int lineNumber) {
            this.label = label;
            this.offset = offset;
            this.lineNumber = lineNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getLabel() {
            return label;
        }

        public int getOffset() {
            return offset;
        }
    }

    private class RelocationLabel {

        private String label;
        private int commandStart;
        private int argumentOffsetFromCommandStart;
        private int lineNumber;

        public RelocationLabel(String label, int commandStart, int argumentOffsetFromCommandStart, int lineNumber) {
            this.label = label;
            this.commandStart = commandStart;
            this.argumentOffsetFromCommandStart = argumentOffsetFromCommandStart;
            this.lineNumber = lineNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getArgumentOffsetFromCommandStart() {
            return argumentOffsetFromCommandStart;
        }

        public int getCommandStart() {
            return commandStart;
        }

        public String getLabel() {
            return label;
        }
    }

    protected class LineAndLabel {

        String line;
        String label;
    }
}
