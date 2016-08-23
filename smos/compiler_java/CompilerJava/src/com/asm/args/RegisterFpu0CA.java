package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterFpu0CA extends RegisterCA{

    public RegisterFpu0CA() {
        addMatcher(new WordMatcher("st0"));
    }

}
