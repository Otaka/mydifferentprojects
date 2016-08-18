package com.asm.args;

import com.asm.args.matchers.NumberMatcher;

/**
 * @author sad
 */
public class Immediate8CA extends ImmediateCA {

    public Immediate8CA() {
        this(null);
    }

    public Immediate8CA(Boolean signed) {
        addMatcher(new NumberMatcher(NumberMatcher.MemSize.BYTE, signed));
        if (signed != null) {
            setSigned(signed);
        }
    }
}
