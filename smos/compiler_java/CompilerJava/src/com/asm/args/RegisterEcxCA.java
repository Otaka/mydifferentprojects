package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterEcxCA extends RegisterCA{

    public RegisterEcxCA() {
        addMatcher(new WordMatcher("ecx"));
    }

}
