package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;

/**
 * @author sad
 */
public abstract class AbstractParser {
    public abstract AbstractParseResult parse(String value, Context context);
}
