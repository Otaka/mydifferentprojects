package com.asm.args.parser.results;

import com.asm.args.parser.NumberParser;

/**
 * @author sad
 */
public class ImmediateResult extends AbstractParseResult{
    private long value;
    private NumberParser.MemSize size;

    public ImmediateResult(long value, NumberParser.MemSize size) {
        this.value = value;
        this.size = size;
    }
    
}
