package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.NumberMatcher;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterAlCA extends RegisterCA{

    public RegisterAlCA() {
         addMatcher(new WordMatcher("al"));
    }

}
