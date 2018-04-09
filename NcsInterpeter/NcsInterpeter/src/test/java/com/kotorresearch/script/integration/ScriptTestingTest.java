package com.kotorresearch.script.integration;

import com.kotorresearch.script.interpreter.FunctionsManager;
import com.kotorresearch.script.MockFunctions;
import com.kotorresearch.script.interpreter.ScriptEvaluator;
import com.kotorresearch.script.data.NwnAction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.AccessException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry
 */
public class ScriptTestingTest {

    @Test
    public void testScrits() throws IOException {
        File directory = new File(ScriptTestingTest.class.getResource(".").toExternalForm().replace("file:/", ""));
        if (!directory.exists()) {
            throw new RuntimeException("For some reason directory with testsuites is not accessible at [" + directory.getAbsolutePath() + "]");
        }

        File dirWithTestFiles = new File(directory, "testsuites");
        File[] testCompiledScripts = dirWithTestFiles.listFiles((dir, name) -> {
            return name.endsWith(".ncs");
        });

        FunctionsManager functionsManager = new FunctionsManager();
        MockFunctions mockFunctions = new MockFunctions();
        functionsManager.addFunctionsHolderObject(mockFunctions);
        if (testCompiledScripts.length == 0) {
            throw new IllegalStateException("Cannot find any test *.ncs file in folder [" + dirWithTestFiles.getAbsolutePath() + "]");
        }

        runTest(new File(dirWithTestFiles, "struct.ncs"), mockFunctions, functionsManager);//test particular file to not wait until others will finish

        //test all files in testsuites folder
        for (File compiledFile : testCompiledScripts) {
            runTest(compiledFile, mockFunctions, functionsManager);
        }
    }

    private void runTest(File compiledFile, MockFunctions mockFunctions, FunctionsManager functionsManager) throws IOException {
        System.out.println("Start testing [" + compiledFile.getName() + "]");
        mockFunctions.reset();
        byte[] buffer = FileUtils.readFileToByteArray(compiledFile);
        ScriptEvaluator evaluator = new ScriptEvaluator(buffer, compiledFile.getName(), functionsManager);
        try {
            evaluator.evaluate();
        } catch (Exception ex) {
            throw new AccessException("Error while test file [" + compiledFile.getName() + "]", ex);
        }

        Assert.assertEquals("Expected, that sp will be 0 after end of script [" + compiledFile.getName() + "] but it is [" + evaluator.getSp() + "]", 0, evaluator.getSp());

        if (!mockFunctions.getDelayedActions().isEmpty()) {
            for (NwnAction action : mockFunctions.getDelayedActions()) {
                action.getClosureScriptEvaluator().evaluate();
            }
        }

        //validate the results againts {script}_result.txt
        File resultsFile = new File(compiledFile.getParentFile(), FilenameUtils.getBaseName(compiledFile.getName()) + "_result.txt");
        if (!resultsFile.exists()) {
            throw new IllegalStateException("Cannot find file with validation results [" + resultsFile.getName() + "] for compiled script [" + compiledFile.getName() + "]");
        }

        List<Integer> integers = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        MutableInt expectedSp = new MutableInt(0);
        loadValidationResults(resultsFile, integers, floats, strings, expectedSp);
        Assert.assertEquals("Printed integers are not match in compiled ncs [" + compiledFile.getName() + "]", integers, mockFunctions.getPrintedIntegers());
        Assert.assertArrayEquals("Printed floats are not match in compiled ncs [" + compiledFile.getName() + "]", toFloatArray(floats), toFloatArray(mockFunctions.getPrintedFloats()), 0.001f);
        Assert.assertEquals("Printed strings are not match in compiled ncs [" + compiledFile.getName() + "]", strings, mockFunctions.getPrintedStrings());
    }

    private float[] toFloatArray(List<Float> floatsList) {
        float[] floats = new float[floatsList.size()];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = floatsList.get(i);
        }
        return floats;
    }

    private void loadValidationResults(File resultsFile, List<Integer> integers, List<Float> floats, List<String> strings, MutableInt expectedSp) throws FileNotFoundException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat floatParser = new DecimalFormat("0.#");
        floatParser.setDecimalFormatSymbols(symbols);

        Scanner scanner = new Scanner(resultsFile);
        int currentMode = -1;
        int VALIDATE_INTEGERS = 0;
        int VALIDATE_STRINGS = 1;
        int VALIDATE_FLOATS = 2;
        int currentLine = 0;
        while (scanner.hasNextLine()) {
            currentLine++;
            String line = scanner.nextLine().trim();
            line = removeComment(line).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("int:")) {//validate printed integers mode
                currentMode = VALIDATE_INTEGERS;
                continue;
            } else if (line.equalsIgnoreCase("string:")) {//validate printed strings mode
                currentMode = VALIDATE_STRINGS;
                continue;
            } else if (line.equalsIgnoreCase("float:")) {//validate printed floats mode
                currentMode = VALIDATE_FLOATS;
                continue;
            } else if (line.startsWith("sp:")) {
                String expectedSpString = line.substring("sp:".length()).trim();
                expectedSp.setValue(parseInteger(expectedSpString));
            }

            if (currentMode == -1) {
                throw new IllegalStateException("File with validation results [" + resultsFile.getName() + "] does not specify mode [int:, string:, float:, byte:], but found line #" + currentLine + " [" + line + "]");
            }

            if (currentMode == VALIDATE_INTEGERS) {
                try {
                    integers.add(parseInteger(line));
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Cannot parse line #" + currentLine + " [" + line + "] in file [" + resultsFile.getName() + "] as integer");
                }
            } else if (currentMode == VALIDATE_FLOATS) {
                try {
                    floats.add(floatParser.parse(line).floatValue());
                } catch (ParseException ex) {
                    throw new IllegalArgumentException("Cannot parse line #" + currentLine + " [" + line + "] in file [" + resultsFile.getName() + "] as float");
                }
            } else if (currentMode == VALIDATE_STRINGS) {
                strings.add(line);
            }
        }
    }

    private String removeComment(String stringValue) {
        int commentIndex = stringValue.indexOf('#');
        if (commentIndex != -1) {
            return stringValue.substring(0, commentIndex);
        }

        return stringValue;
    }

    private int parseInteger(String value) {
        if (value.startsWith("0x")) {
            return Integer.parseInt(value.substring(2), 16);
        } else {
            return Integer.parseInt(value);
        }
    }
}
