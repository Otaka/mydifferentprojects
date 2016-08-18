package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterTRegCA extends RegisterCA{

    public RegisterTRegCA() {
        addMatcher(new WordMatcher("tr3","tr4","tr5","tr6","tr7"));
    }

}
