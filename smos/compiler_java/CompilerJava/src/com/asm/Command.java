package com.asm;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sad
 */
public class Command {

    private Set<Architecture> architectures = new HashSet<>();
    private String mnemonic;
    private int opcode;
    private CommandArgument[] args;

    public Command(String mnemonic, int opcode) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
    }

    public int getArgumentsCount() {
        return args.length;
    }

    public void setArchitectures(Architecture[] architectures) {
        this.architectures.clear();
        for (Architecture ar : architectures) {
            this.architectures.add(ar);
        }
    }

    public Set<Architecture> getArchitectures() {
        return architectures;
    }

    /*public void setArg1(CommandArgument arg1) {
     this.arg1 = arg1;
     }

     public void setArg2(CommandArgument arg2) {
     this.arg2 = arg2;
     }

     public void setArg3(CommandArgument arg3) {
     this.arg3 = arg3;
     }

     public CommandArgument getArg1() {
     return arg1;
     }

     public CommandArgument getArg2() {
     return arg2;
     }

     public CommandArgument getArg3() {
     return arg3;
     }*/
    public CommandArgument[] getArgs() {
        return args;
    }

    public void setArgs(CommandArgument[] args) {
        this.args = args;
    }
    
    

    public String getMnemonic() {
        return mnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    @Override
    public String toString() {
        return String.format("0x%x:%s", opcode, mnemonic);
    }

}
