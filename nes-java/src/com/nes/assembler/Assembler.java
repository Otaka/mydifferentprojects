package com.nes.assembler;

import com.gooddies.regexp.RegExpExtractor;
import com.nes.processor.commands.Commands;
import com.nes.processor.memory_addressing.ABS;
import com.nes.processor.memory_addressing.ABS16;
import com.nes.processor.memory_addressing.ABS_X;
import com.nes.processor.memory_addressing.ABS_Y;
import com.nes.processor.memory_addressing.ACC;
import com.nes.processor.memory_addressing.AbstractMemoryAdressing;
import com.nes.processor.memory_addressing.IMMEDIATE;
import com.nes.processor.memory_addressing.IMPLISIT;
import com.nes.processor.memory_addressing.INDIRECT;
import com.nes.processor.memory_addressing.IND_X;
import com.nes.processor.memory_addressing.IND_Y;
import com.nes.processor.memory_addressing.REL;
import com.nes.processor.memory_addressing.ZP;
import com.nes.processor.memory_addressing.ZP_X;
import com.nes.processor.memory_addressing.ZP_Y;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry
 */
public class Assembler {

    private Map<String, List<Instruction>> instructions;
    private Map<Byte, String> instructionToString=new HashMap<>();
    private Map<String, AbstractMemoryAdressing> addressingMap;
    private Map<String, Integer> labels;
    private final List<LabelToReplace> labelsToReplace;
    private int startOffset = 0x600;
    private final RegExpExtractor labelRegExp = new RegExpExtractor("[A-Za-z_]{1}[A-Za-z0-9_]*");

    public Assembler() {
        this.labelsToReplace = new ArrayList<>();
        init();
    }

    private void init() {
        this.instructions = new HashMap<>();
        labels = new HashMap<>();
        fillAddressingMap();

        Class clazz = Commands.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                if (f.getType() == byte.class) {
                    Instruction instruction = processField(f);
                    addInstruction(instruction);
                }
            }
        }
        sortInstructionList();
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    private void addInstruction(Instruction instruction) {
        String name = instruction.getName();
        List<Instruction> list = instructions.get(name);
        if (list == null) {
            list = new ArrayList<>();
            instructions.put(name, list);
        }
        list.add(instruction);
        instructionToString.put(instruction.getCommand(), name);
    }
    
    private void sortInstructionList(){
        for(List<Instruction>instructionList:instructions.values()){
            Collections.sort(instructionList);
        }
    }

    private void fillAddressingMap() {
        this.addressingMap = new HashMap<>();
        addressingMap.put("ABS", new ABS());
        addressingMap.put("ABS16", new ABS16());
        addressingMap.put("ABS_X", new ABS_X());
        addressingMap.put("ABS_Y", new ABS_Y());
        addressingMap.put("ACC", new ACC());
        addressingMap.put("ACC", new ACC());
        addressingMap.put("ACC", new ACC());
        addressingMap.put("IMM", new IMMEDIATE());
        addressingMap.put("IMP", new IMPLISIT());
        addressingMap.put("IND", new INDIRECT());
        addressingMap.put("IND_X", new IND_X());
        addressingMap.put("IND_Y", new IND_Y());
        addressingMap.put("REL", new REL());
        addressingMap.put("ZP", new ZP());
        addressingMap.put("ZP_X", new ZP_X());
        addressingMap.put("ZP_Y", new ZP_Y());
    }

    private AbstractMemoryAdressing getAddressing(String addressing) {
        if (!addressingMap.containsKey(addressing)) {
            throw new RuntimeException("No such addressing " + addressing);
        }
        return addressingMap.get(addressing);
    }

    private Instruction processField(Field f) {
        String name = f.getName();
        byte command;
        try {
            command = (byte) f.get(null);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }

        String commandMnemonic;
        String maString = "";
        int indexOfDollar=name.indexOf('$');
        if(indexOfDollar!=-1){
            name=name.substring(0, indexOfDollar);
        }
        int _ind = name.indexOf("_");
        if (_ind == -1) {
            commandMnemonic = name;
        } else {
            commandMnemonic = name.substring(0, _ind);
            maString = name.substring(_ind + 1);
        }

        AbstractMemoryAdressing ma;
        

        if (maString.isEmpty()) {
            ma = getAddressing("IMP");
        } else {
            ma = getAddressing(maString);
        }

        Instruction instruction = new Instruction();
        instruction.setCommand(command);
        instruction.setMa(ma);
        instruction.setName(commandMnemonic);
        return instruction;
    }

    public byte[] assemble(String line) {
        if (line == null) {
            return null;
        }
        String[] lines = line.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replace('\r', ' ');
        }
        return assemble(lines);
    }

    public byte[] assemble(String[] lines) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(500);
        int lineIndex = -1;
        for (String str : lines) {
            lineIndex++;
            String codeLine = stripString(str, bytes.size(), lineIndex);
            if (!codeLine.isEmpty()) {
                processString(codeLine, bytes, lineIndex);
            }
        }

        byte[] resultBytes = bytes.toByteArray();
        processLabels(resultBytes);
        return resultBytes;
    }

    private void processLabels(byte[] bytesStream) {
        for (LabelToReplace l : labelsToReplace) {
            if (!labels.containsKey(l.getLabel())) {
                throw new ParseException("Cannot find label '" + l.getLabel() + "'", l.getLineNumber());
            }

            int bytesPosition = labels.get(l.getLabel());
            if (l.getSize() == 2) {
                int position = bytesPosition + startOffset;
                bytesStream[l.getBytePosition()] = (byte) (position & 0xFF);
                bytesStream[l.getBytePosition() + 1] = (byte) ((position >> 8) & 0xFF);
            } else {
                int offset = bytesPosition - 1 - l.getBytePosition();
                if (offset < -126 || offset > 129) {
                    throw new ParseException("Branch try to jump very far label", l.getLineNumber());
                }
                bytesStream[l.getBytePosition()] = (byte) (offset & 0xFF);
            }
        }
    }

    private void addLabel(String label, int position) {
        label = label.toUpperCase();
        if (labels.containsKey(label)) {
            throw new RuntimeException("Label " + label + " already exist on line:" + labels.get(label));
        }

        labels.put(label, position);
    }

    private String stripString(String str, int currentByteIndex, int lineNumber) {
        str = str.trim().toUpperCase();
        int semiColonIndex = str.indexOf(';');
        if (semiColonIndex != -1) {
            str = str.substring(0, semiColonIndex).trim();
        }

        int colonIndex = str.indexOf(':');
        if (colonIndex != -1) {
            String label = str.substring(0, colonIndex);
            if (!labelRegExp.match(label)) {
                throw new ParseException("Label " + label + " has wrong format", lineNumber);
            }

            addLabel(label, currentByteIndex);
            str = str.substring(colonIndex + 1);
        }

        return str.replace("[\\r\\t\\s]", " ");
    }

    private void processString(String line, ByteArrayOutputStream bytes, int lineNumber) {
        int spaceIndex = line.indexOf(' ');
        String command;
        String commandArg;
        if (spaceIndex == -1) {
            commandArg = "";
            command = line;
        } else {
            command = line.substring(0, spaceIndex).trim();
            commandArg = line.substring(spaceIndex + 1).trim();
        }

        List<Instruction> instructionList = instructions.get(command);
        if (instructionList == null) {
            throw new ParseException("Cannot find such command " + command, lineNumber);
        }

        byte[] argumentMemoryBytes = null;
        byte commandToWrite = -1;
        String label = null;
        for (Instruction i : instructionList) {
            AbstractMemoryAdressing ma = i.getMa();
            argumentMemoryBytes = ma.parseFromString(commandArg);
            commandToWrite = i.getCommand();
            if (argumentMemoryBytes != null) {
                label = ma.getLabel(commandArg);
                break;
            }
        }

        if (argumentMemoryBytes == null) {
            throw new ParseException("Cannot parse line " + line, lineNumber);
        }

        bytes.write(commandToWrite);
        if (label != null) {
            LabelToReplace l = new LabelToReplace(label, bytes.size(), argumentMemoryBytes.length, lineNumber);
            labelsToReplace.add(l);
        }
        try {
            bytes.write(argumentMemoryBytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public String getInstructionName(byte instruction){
        return instructionToString.get(instruction);
    }
    
    
}
