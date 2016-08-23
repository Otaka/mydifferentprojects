package com.asm.args;

import com.asm.args.matchers.NumberMatcher;

/**
 * @author sad
 */
public class Immediate16CA extends ImmediateCA {

    public Immediate16CA() {
        this(null);
    }

    public Immediate16CA(Boolean signed) {
        addMatcher(new NumberMatcher(NumberMatcher.MemSize.WORD, signed));
        if (signed != null) {
            setSigned(signed);
        }
    }

}
