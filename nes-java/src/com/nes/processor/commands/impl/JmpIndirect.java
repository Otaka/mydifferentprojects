package com.nes.processor.commands.impl;

import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.commands.AbstractCommand;

/**
 * @author Dmitry
 */
public class JmpIndirect extends AbstractCommand {

    @Override
    public byte execute(ALU alu, byte input) {
        byte lOfPointer = alu.getNextByte();
        byte hiOfPointer = alu.getNextByte();
        int pointer = AluUtils.lhTo16Bit(lOfPointer, hiOfPointer);
        byte low = alu.getMemory().getValue(pointer);
        byte hi;
        if(lOfPointer==-1){//if last page bug
            pointer = AluUtils.lhTo16Bit((byte)0, hiOfPointer);//AluUtils.i16ToHexString(pointer);
            hi = alu.getMemory().getValue(pointer);
        }else{
            hi = alu.getMemory().getValue(pointer+1);
        }
        int offset = AluUtils.lhTo16Bit(low, hi);
        AluUtils.i16ToHexString(offset);//AluUtils.i16ToHexString(offset)
        alu.setPc(offset);
        return 0;
    }
}
