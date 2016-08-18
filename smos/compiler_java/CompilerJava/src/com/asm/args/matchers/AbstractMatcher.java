package com.asm.args.matchers;

import com.asm.args.argresult.AbstractParsingResult;

/**
 * @author sad
 */
public abstract class AbstractMatcher {

    public abstract AbstractParsingResult match(String value);
}
