package com.asm.args;

import com.asm.args.matchers.AbstractMatcher;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class RegisterEsCA extends RegisterCA {

    @Override
    public void addMatcher(AbstractMatcher matcher) {
        super.addMatcher(new WordMatcher("es"));
    }
}
