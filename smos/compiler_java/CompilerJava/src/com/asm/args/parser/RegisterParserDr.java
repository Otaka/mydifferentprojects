package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;
import com.asm.args.parser.results.RegisterResult;

/**
 * @author sad
 */
public class RegisterParserDr extends AbstractParser {

    private String[] registers = new String[]{"dr0", "dr1", "dr2", "dr3","dr6","dr7"};
    private int[] codes = new int[]{0b000, 0b001, 0b010, 0b011,0b110,0b111};

    @Override
    public AbstractParseResult parse(String value, Context context) {
        value = value.toLowerCase();
        for (int i = 0; i < registers.length; i++) {
            if (value.equals(registers[i])) {
                return new RegisterResult(registers[i], 32, codes[i], RegisterResult.REG_TYPE.CONTROL);
            }
        }

        return null;
    }

}
