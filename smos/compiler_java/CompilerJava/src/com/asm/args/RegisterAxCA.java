package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterAxCA extends RegisterCA{

    public RegisterAxCA() {
         addMatcher(new WordMatcher("ax"));
    }

}
