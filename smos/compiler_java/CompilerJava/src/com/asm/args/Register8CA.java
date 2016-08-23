package com.asm.args;

import com.asm.CommandArgument;
import com.asm.args.matchers.AbstractMatcher;
import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class Register8CA extends RegisterCA{

    @Override
    public void addMatcher(AbstractMatcher matcher) {
        super.addMatcher(new WordMatcher("ah","al","bh","bl","ch","cl","dh","dl"));
    }

}
