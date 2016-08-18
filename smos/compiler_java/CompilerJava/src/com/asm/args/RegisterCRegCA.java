package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterCRegCA extends RegisterCA{

    public RegisterCRegCA() {
        addMatcher(new WordMatcher("cr0","cr1","cr2","cr3","cr4"));
    }

}
