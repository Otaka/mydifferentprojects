package com.asm.args;

import com.asm.CommandArgument;

/**
 * @author sad
 */
public class RegisterCA extends CommandArgument {

    boolean embed = false;

    public RegisterCA setEmbed(boolean embed) {
        this.embed = embed;
        return this;
    }

    public boolean isEmbed() {
        return embed;
    }

    @Override
    public boolean isReg() {
        return true;
    }

}
