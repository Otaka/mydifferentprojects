package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterDsCA extends RegisterCA{

    public RegisterDsCA() {
        super.addMatcher(new WordMatcher("ds"));
    }

}
