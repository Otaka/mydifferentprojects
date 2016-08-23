package com.asm.args;

import com.asm.CommandArgument;

/**
 * @author sad
 */
public abstract class ImmediateCA extends CommandArgument {

    private Boolean signed;

    protected ImmediateCA setSigned(boolean signed) {
        this.signed = signed;
        return this;
    }

    public boolean isSigned() {
        return signed;
    }

    @Override
    public boolean isImm() {
        return true;
    }
    
    

}
