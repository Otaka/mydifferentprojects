package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterDRegCA extends RegisterCA{

    public RegisterDRegCA() {
         addMatcher(new WordMatcher("dr0","dr1","dr2","dr3","dr4","dr5","dr6","dr7"));
    }

}
