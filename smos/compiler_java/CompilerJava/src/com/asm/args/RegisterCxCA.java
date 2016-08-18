package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterCxCA extends RegisterCA{

    public RegisterCxCA() {
         addMatcher(new WordMatcher("cx"));
    }

}
