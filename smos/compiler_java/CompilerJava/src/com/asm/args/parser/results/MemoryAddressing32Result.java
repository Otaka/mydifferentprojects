package com.asm.args.parser.results;

/**
 * @author sad
 */
public class MemoryAddressing32Result extends AbstractParseResult {

    private String line;
    private int mod;
    private int rm;
    private long offset;
    private int sib=-1;

    public MemoryAddressing32Result(String line, int mod, int rm, long offset) {
        this.line = line;
        this.mod = mod;
        this.rm = rm;
        this.offset = offset;
    }

    public MemoryAddressing32Result setSib(int sib) {
        this.sib = sib;
        return this;
    }

    public int getSib() {
        return sib;
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

    @Override
    public String toString() {
        if(sib==-1){
            return "";
        }
        return "sib:"+Integer.toHexString(sib);
    }

    
}
