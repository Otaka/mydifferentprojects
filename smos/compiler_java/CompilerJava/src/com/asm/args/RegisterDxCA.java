package com.asm.args;

import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterDxCA extends RegisterCA {

    public RegisterDxCA() {
        addMatcher(new WordMatcher("dx"));
    }

}
