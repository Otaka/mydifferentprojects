package com.sqlprocessor.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class SourceCode {

    private List<String> initializationSourceCode = new ArrayList<>();
    private int nextFreeId = -1;//just some id for usage

    public int getNextFreeId() {
        nextFreeId++;
        return nextFreeId;
    }

    public void addInitializationSourceCode(String line) {
        initializationSourceCode.add(line);
    }

    public List<String> getInitializationSourceCode() {
        return initializationSourceCode;
    }

}
