package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterClCA extends RegisterCA {

    public RegisterClCA() {
         addMatcher(new WordMatcher("cl"));
    }

  

}
