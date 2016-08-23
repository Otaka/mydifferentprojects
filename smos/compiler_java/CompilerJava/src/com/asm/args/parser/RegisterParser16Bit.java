package com.asm.args.parser;

import com.asm.Context;
import com.asm.args.parser.results.AbstractParseResult;
import com.asm.args.parser.results.RegisterResult;

/**
 * @author sad
 */
public class RegisterParser16Bit extends AbstractParser {

    private String[] registers = new String[]{"ax", "cx", "dx", "bx", "sp", "bp", "si", "di"};
    private int[] codes = new int[]{0b000, 0b001, 0b010, 0b011, 0b100, 0b101, 0b110, 0b111};

    @Override
    public AbstractParseResult parse(String value, Context context) {
        value = value.toLowerCase();
        for (int i = 0; i < registers.length; i++) {
            if (value.equals(registers[i])) {
                return new RegisterResult(registers[i], 16, codes[i], RegisterResult.REG_TYPE.GENERAL);
            }
        }

        return null;
    }

}
