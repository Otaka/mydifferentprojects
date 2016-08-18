package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterGsCA extends RegisterCA{

    public RegisterGsCA() {
        super.addMatcher(new WordMatcher("gs"));
    }

}
