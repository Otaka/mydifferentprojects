package com.kotorresearch.script;

import com.kotorresearch.script.interpreter.ScriptFunctionAnnotation;
import com.kotorresearch.script.data.NwnAction;
import com.kotorresearch.script.data.NwnVector;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class MockFunctions {

    private List<String> strings = new ArrayList<>();
    private List<Integer> integers = new ArrayList<>();
    private List<Float> floats = new ArrayList<>();
    private String currentSection;
    private List<NwnAction>delayedActions=new ArrayList<>();

    //return number 4, because it is mock test
    @ScriptFunctionAnnotation(index = 0)
    public int Random(int maxValue) {
        return 4;
    }

    @ScriptFunctionAnnotation(index = 1)
    public void printString(String value) {
        System.out.println("PrintString:[" + value + "]");
        if (!value.startsWith("LABEL:")) {
            strings.add(value);
        }
    }

    @ScriptFunctionAnnotation(index = 2)
    public void printFloat(float value, int nWidth, int nDecimals) {
        System.out.println("PrintFloat:[" + value + "," + nWidth + "," + nDecimals + "]");
        floats.add(value);
    }

    @ScriptFunctionAnnotation(index = 4)
    public void printInteger(int value) {
        System.out.println("PrintInteger:[" + value + "]");
        integers.add(value);
    }
    
    @ScriptFunctionAnnotation(index = 7)
    public void delayAction(float time,NwnAction savedClosureAction) {
        System.out.println("Delay action");
        delayedActions.add(savedClosureAction);
    }
    
    

    //just returns argument + 1
    @ScriptFunctionAnnotation(index = 68)
    public float cos(float value) {
        return value + 1;
    }

    @ScriptFunctionAnnotation(index = 142)
    public NwnVector vector(float x, float y, float z) {
        return new NwnVector(x, y, z);
    }

    //in tests it is used to specify the name of section that is executed. To divide long tests
    @ScriptFunctionAnnotation(index = 160)
    public void SetGlobalString(String identifier, String value) {
        currentSection = identifier;
        System.out.println("Section " + identifier);
    }

    public List<NwnAction> getDelayedActions() {
        return delayedActions;
    }

    
    
    public List<String> getPrintedStrings() {
        return strings;
    }

    public List<Float> getPrintedFloats() {
        return floats;
    }

    public List<Integer> getPrintedIntegers() {
        return integers;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public void reset() {
        delayedActions.clear();
        strings.clear();
        integers.clear();
        floats.clear();
        currentSection = "not_specified";
    }

}
