package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterEaxCA extends RegisterCA{

    public RegisterEaxCA() {
        addMatcher(new WordMatcher("eax"));
    }

}
