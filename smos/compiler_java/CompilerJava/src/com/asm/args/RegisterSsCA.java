package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterSsCA extends RegisterCA{

    public RegisterSsCA() {
        super.addMatcher(new WordMatcher("ss"));
    }

}
