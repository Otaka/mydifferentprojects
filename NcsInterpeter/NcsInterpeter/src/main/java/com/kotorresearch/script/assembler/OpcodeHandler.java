package com.kotorresearch.script.assembler;

import java.util.regex.Pattern;

/**
 * @author Dmitry
 */
public class OpcodeHandler {

    Pattern pattern;
    int code;
    int type;
    private boolean hasType;
    CustomLineAssembler customAssembler;
    private OpcodeArgumentType[] types;
    private boolean hasRelocation = false;
    private boolean used;

    public OpcodeHandler(String pattern, int code, int type, boolean hasType, CustomLineAssembler lineProcessor) {
        this.pattern = Pattern.compile(pattern);
        this.code = code;
        this.type = type;
        this.hasType = hasType;
        this.customAssembler = lineProcessor;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }

    public OpcodeHandler(String pattern, int code, int type, boolean hasType) {
        this.pattern = Pattern.compile(pattern);
        this.code = code;
        this.type = type;
        this.hasType = hasType;
        this.customAssembler = null;
    }

    public OpcodeHandler setHasRelocation(boolean hasRelocation) {
        this.hasRelocation = hasRelocation;
        return this;
    }

    public boolean isHasRelocation() {
        return hasRelocation;
    }

    public OpcodeArgumentType[] getArguments() {
        return types;
    }

    public OpcodeHandler setArgumentTypes(OpcodeArgumentType... types) {
        this.types = types;
        return this;
    }

    public boolean isHasType() {
        return hasType;
    }

    public int getCode() {
        return code;
    }

    public CustomLineAssembler getCustomAssembler() {
        return customAssembler;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getType() {
        return type;
    }

}
