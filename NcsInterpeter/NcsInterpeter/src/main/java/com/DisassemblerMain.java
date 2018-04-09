package com;

import com.kotorresearch.script.utils.ByteArrayUtils;
import com.kotorresearch.script.assembler.NcsAssembler;
import com.kotorresearch.script.assembler.OpcodeHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class DisassemblerMain {

    private static Set<String> foundActionFunctions = new HashSet<>();

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File scriptFolder = new File("f:\\java\\mydifferentprojects\\NcsInterpeter\\NcsInterpeter\\src\\test\\java\\com\\kotorresearch\\script\\integration\\testsuites\\");
        //File scriptFolder = new File("g:\\kotor_Extracted\\extracted\\bif\\scripts_with_sources\\");
        for (File f : scriptFolder.listFiles()) {
            if (f.getAbsolutePath().endsWith(".ncs")) {
                System.out.println("process file " + f.getAbsolutePath() + " " + f.length() + "b");
                run(new String[]{f.getAbsolutePath()});
            }
        }

        System.out.println("Unused opcodes:");
        for (OpcodeHandler h : NcsAssembler.opcodeHandlers) {
            if (!h.isUsed()) {
                System.out.println(h.getPattern());
            }
        }

        Scanner scanner = new Scanner(DisassemblerMain.class.getResourceAsStream("/functionsList.txt"));
        try (PrintStream outputStream = new PrintStream("f:/function.txt")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = StringUtils.splitPreserveAllTokens(line, '\t');
                String functionName = parts[2];
                if (foundActionFunctions.contains(functionName)) {
                    outputStream.println(line + "\tused");
                } else {
                    outputStream.println(line + "\tnot_used");
                }
            }
        }
    }

    public static void run(String[] args) throws IOException {
        if (args.length == 0 || args.length >= 3) {
            System.out.println("NCS decompiler. Example of usage: java -jar ncs4j.jar *.ncs [output_file]");
            return;
        }

        String inputFile = null;
        String outputFile = null;
        if (args.length == 1) {
            inputFile = args[0];
            File inputFileObject = new File(inputFile);
            if (!inputFileObject.exists()) {
                System.out.println("Input file [" + inputFile + "] not exists");
                return;
            }

            outputFile = inputFile + ".disassembled";
        } else if (args.length == 2) {
            inputFile = args[0];
            outputFile = args[1];
            File inputFileObject = new File(inputFile);
            if (!inputFileObject.exists()) {
                System.out.println("Input file [" + inputFile + "] not exists");
                return;
            }
        }

        NcsAssembler assembler = new NcsAssembler();
        File inputFileObject = new File(inputFile);
        byte[] buffer = new byte[(int) inputFileObject.length()];
        try (FileInputStream inputStream = new FileInputStream(inputFileObject)) {
            inputStream.read(buffer);
        }

        List<NcsAssembler.DisassembledLine> lines = assembler.disassembler(buffer);
        try (PrintStream printStream = new PrintStream(new File(outputFile))) {
            for (NcsAssembler.DisassembledLine line : lines) {
                if (line.getAttributes().get("opcode").equals("ACTION")) {
                    foundActionFunctions.add((String) line.getAttributes().get("functionName"));
                }
                String addressString = ByteArrayUtils.hex(line.getOffset(), 4);
                String lineString = "/*" + addressString + " " + StringUtils.rightPad(line.getByteDumpString(), 30, ' ') + "*/  " + line.getLine();
                printStream.println(lineString);
            }
        }
    }
}
