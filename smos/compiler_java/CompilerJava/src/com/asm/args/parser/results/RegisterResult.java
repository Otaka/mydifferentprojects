package com.asm.args.parser.results;

/**
 * @author sad
 */
public class RegisterResult extends AbstractParseResult{
    public enum REG_TYPE{
        GENERAL, CONTROL, DEBUG, FP
    }

    private String register;
    private int bits;
    private int code;
    private REG_TYPE regType;

    public RegisterResult(String register, int bits, int code, REG_TYPE regType) {
        this.register = register;
        this.bits = bits;
        this.code = code;
        this.regType=regType;
    }

    public REG_TYPE getRegType() {
        return regType;
    }

    
    
    public int getBits() {
        return bits;
    }

    public int getCode() {
        return code;
    }

    public String getRegister() {
        return register;
    }

}
