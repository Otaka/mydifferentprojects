package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterFpuRegCA extends RegisterCA{

    public RegisterFpuRegCA() {
        addMatcher(new WordMatcher("st0","st1","st2","st3","st4","st5","st6","st7"));
    }

}
