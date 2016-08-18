package com.asm.args.argresult;

import com.asm.args.matchers.NumberMatcher;

/**
 * @author sad
 */
public class NumberResult extends AbstractParsingResult{
    private NumberMatcher.MemSize memSize;
    private long value;

    public NumberResult(NumberMatcher.MemSize memSize, long value) {
        this.memSize = memSize;
        this.value = value;
    }

    public NumberMatcher.MemSize getMemSize() {
        return memSize;
    }

    public long getValue() {
        return value;
    }
    
}
