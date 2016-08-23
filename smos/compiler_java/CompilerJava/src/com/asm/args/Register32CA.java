package com.asm.args;

import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class Register32CA extends RegisterCA {

    public Register32CA() {
        addMatcher(new WordMatcher("eax", "ebx", "ecx", "edx"));
    }

}
