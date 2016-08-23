package com.asm.args;

import com.asm.args.matchers.WordMatcher;

/**
 * @author sad
 */
public class Register16CA extends RegisterCA {

    public Register16CA() {
        addMatcher(new WordMatcher("ax","bx","cx","dx"));
    }

}
