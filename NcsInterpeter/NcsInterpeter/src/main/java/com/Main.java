package com;

import com.kotorresearch.script.interpreter.FunctionsManager;
import com.kotorresearch.script.interpreter.ScriptEvaluator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Started NSS script evaluator");
        byte[] buffer = readWholeFileToBuffer("g:\\kotor_Extracted\\extracted\\bif\\scripts\\k_pman_planet10.ncs");
        FunctionsManager functionsManager=new FunctionsManager();
        ScriptEvaluator scriptEvaluator = new ScriptEvaluator(buffer,"k_pman_planet10",functionsManager);
        scriptEvaluator.evaluate();
        System.out.println("Finish");
    }

    private static byte[] readWholeFileToBuffer(String filePath) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        int length = (int) file.length();
        byte[] buffer = new byte[length];
        try (FileInputStream stream = new FileInputStream(file)) {
            int readCount = stream.read(buffer);
            if (readCount != length) {
                throw new IllegalStateException("Expected to read [" + length + "] but read [" + readCount + "] bytes.");
            }
        }

        return buffer;
    }
}
