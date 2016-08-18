package com.asm.args.parser.results;

/**
 * @author sad
 */
public class MemoryAddressing16Result extends AbstractParseResult {

    private String line;
    private int mod;
    private int rm;
    private long offset;

    public MemoryAddressing16Result(String line, int mod, int rm, long offset) {
        this.line = line;
        this.mod = mod;
        this.rm = rm;
        this.offset = offset;
    }

    public String getLine() {
        return line;
    }

    public int getMod() {
        return mod;
    }

    public int getRm() {
        return rm;
    }

    public long getOffset() {
        return offset;
    }

}
