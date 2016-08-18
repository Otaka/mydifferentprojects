package com.asm.args;

import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterFsCA extends RegisterCA {

    public RegisterFsCA() {
        super.addMatcher(new WordMatcher("fs"));
    }

}
