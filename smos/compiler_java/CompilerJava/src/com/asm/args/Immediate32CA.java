package com.asm.args;

import com.asm.args.matchers.NumberMatcher;

/**
 * @author sad
 */
public class Immediate32CA extends ImmediateCA {

    public Immediate32CA() {
        this(null);
    }

    public Immediate32CA(Boolean signed) {
        addMatcher(new NumberMatcher(NumberMatcher.MemSize.DWORD, signed));
        if (signed != null) {
            setSigned(signed);
        }
    }
}
