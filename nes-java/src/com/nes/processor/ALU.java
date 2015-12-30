package com.nes.processor;

import com.nes.assembler.Assembler;
import com.nes.processor.commands.AbstractCommand;
import com.nes.processor.commands.AbstractCommand.StoreTo;
import com.nes.processor.commands.CommandDefinition;
import com.nes.processor.commands.CommandsDefinitionArray;
import com.nes.processor.memory_addressing.AbstractMemoryAdressing;

/**
 * @author sad
 */
public class ALU {

    private byte a;
    private byte x;
    private byte y;
    private int s = 0xFD;
    private int pc = 0x8000;
    private final AluFlags aluFlags = new AluFlags();
    private Memory memory;
    private CommandsDefinitionArray commandsDefinitionArray;
    private Assembler assembler = new Assembler();

    public ALU() {
        memory = new SimpleMemory();
        init();
    }

    private void init() {
        commandsDefinitionArray = new CommandsDefinitionArray();
    }

    public AluFlags getAluFlags() {
        return aluFlags;
    }

    public byte getA() {
        return a;
    }

    public void setA(byte a) {
        this.a = a;
    }

    public byte getX() {
        return x;
    }

    public void setX(byte x) {
        this.x = x;
    }

    public byte getY() {
        return y;
    }

    public void setY(byte y) {
        this.y = y;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = AluUtils.unsignedByte((byte) s);
    }

    public int getPc() {
        return pc;
    }

    public void addToPc(byte offset) {
        pc += offset;
    }

    public void add16BitToPc(int offset) {
        pc += offset;
    }

    public void pcIncrement() {
        pc++;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Memory getMemory() {
        return memory;
    }

    public byte getNextByte() {
        int pcvalue = getPc();
        pcIncrement();
        byte command = memory.getValue(pcvalue);
        return command;
    }

    public void executeCommand() {
        byte commandByte = getNextByte();
        CommandDefinition definition = commandsDefinitionArray.getCommandDefinition(commandByte);
        AbstractCommand command = definition.getCommand();
        AbstractMemoryAdressing ma = definition.getAbstractMemoryAdressing();
        ma.calculateIndex(this);
        byte value = ma.getValue(this);

        byte result = command.execute(this, value);
        AbstractCommand.StoreTo storeTo = command.writeMemory();
        if (command.shouldSetNZ()) {
            setNZFlag(result);
        }
        if (storeTo != StoreTo.None) {
            if (storeTo == StoreTo.Accumulator) {
                setA(result);
            } else {
                ma.setValue(this, result);
            }
        }
    }
    
    public void setNZFlag(byte value){
        aluFlags.setZero(value == 0);
        aluFlags.setNegative((value & 0x80) != 0);
    }

    public byte add(byte v1, byte v2) {
        /* if (aluFlags.isDecimal()) {
         return addBCD(v1, v2);
         } else {*/
        return addBinary(v1, v2);
        //}
    }

    public byte sub(byte v1, byte v2) {
        v2 = (byte) (AluUtils.unsignedByte(v2) + 1);
        v2 = (byte) (v2 * -1);
        /*if (aluFlags.isDecimal()) {
         return addBCD(v1, v2);
         } else {*/
        return addBinary(v1, v2);
        //  }
    }

    private byte addBCD(byte v1Arg, byte v2Arg) {
        int v1 = AluUtils.unsignedByte(v1Arg);
        int v2 = AluUtils.unsignedByte(v2Arg);
        int carry = getAluFlags().isCarry() ? 1 : 0;
        int tmp = (v1 & 0xf) + (v2 & 0xf) + carry;
        if (tmp >= 10) {
            tmp = 0x10 | ((tmp + 6) & 0xf);
        }

        tmp += (v1 & 0xf0) + (v2 & 0xf0);
        if (tmp >= 160) {
            getAluFlags().setCarry(true);
            if (getAluFlags().isOverflow() && tmp >= 0x180) {
                getAluFlags().setOverflow(false);
            }
            tmp += 0x60;
        } else {
            getAluFlags().setCarry(false);
            if (getAluFlags().isOverflow() && tmp < 0x80) {
                getAluFlags().setOverflow(false);
            }
        }

        return (byte) (tmp & 0xff);
    }


    /* private byte addBCD(byte v1Arg, byte v2Arg) {
     int v1 = AluUtils.unsignedByte(v1Arg);
     int v2 = AluUtils.unsignedByte(v2Arg);
     int carry = getAluFlags().isCarry() ? 1 : 0;
     int tmp = (v1 & 0xf) + (v2 & 0xf) + carry;
     if (tmp >= 10) {
     tmp = tmp - 10;
     carry = 1;
     } else {
     carry = 0;
     }
     int tmp2 = ((v1 >> 4) & 0xf) + ((v2 >> 4) & 0xf)+carry;
     if (tmp2 >= 10) {
     tmp2=tmp2-10;
     getAluFlags().setOverflow(true);
     }
     tmp += (tmp2 << 4);
     if (tmp >= 160) {
     getAluFlags().setCarry(true);
     if (getAluFlags().isOverflow() && tmp >= 0x180) {
     getAluFlags().setOverflow(false);
     }
     tmp += 0x60;
     } else {
     getAluFlags().setCarry(false);
     if (getAluFlags().isOverflow() && tmp < 0x80) {
     getAluFlags().setOverflow(false);
     }
     }

     return (byte) (tmp & 0xff);
     }*/
    private void CLV() {
        aluFlags.setOverflow(false);
    }

    private void CLC() {
        aluFlags.setCarry(false);
    }

    private void SEC() {
        aluFlags.setCarry(true);
    }

    private void setOverflow() {
        aluFlags.setOverflow(true);
    }

    private boolean overflowSet() {
        return aluFlags.isOverflow();
    }

    private boolean carrySet() {
        return aluFlags.isCarry();
    }

    private byte addBinary(byte arg1, byte arg2) {
        int tmp;
        int v1 = AluUtils.unsignedByte(arg1);
        int v2 = AluUtils.unsignedByte(arg2);
        if (((v1 ^ v2) & 0x80) != 0) {
            CLV();
        } else {
            setOverflow();
        }

        tmp = v1 + v2 + (carrySet() ? 1 : 0);
        if (tmp >= 0x100) {
            SEC();
            if (overflowSet() && tmp >= 0x180) {
                CLV();
            }
        } else {
            CLC();
            if (overflowSet() && tmp < 0x80) {
                CLV();
            }
        }

        v1 = (byte) (tmp & 0xff);
        if ((v1 & 0x80) != 0) {
            aluFlags.setNegative(true);
        } else {
            aluFlags.setNegative(false);
        }
        return (byte) v1;
    }

    public void pushToStack(byte value) {
        int index = s + 0x100;
        getMemory().setValue(value, index);
        s--;
    }

    public byte popFromStack() {
        s++;
        int index = s + 0x100;
        return getMemory().getValue(index);
    }

    public String decodeCommand(boolean fullDescription) {
        int oldPc = pc;
        byte commandByte = getNextByte();
        CommandDefinition definition = commandsDefinitionArray.getCommandDefinition(commandByte);
        AbstractMemoryAdressing ma = definition.getAbstractMemoryAdressing();

        String commandName = assembler.getInstructionName(commandByte);
        String memoryString = ma.getStringDefinition(this);
        memoryString = memoryString.toUpperCase();
        int currentPc = pc;
        pc = oldPc;
        if (!fullDescription) {
            return commandName + " " + memoryString;
        }

        byte[] commandBytes = new byte[currentPc - oldPc];
        fillArrayFromMemory(commandBytes, oldPc);
        String bytes = AluUtils.bytesToHexString(commandBytes);

        StringBuilder sb = new StringBuilder();
        sb.append(AluUtils.i16ToHexString(oldPc));//ADDRESS
        sb.append("  ");

        sb.append(ensureStringLength(AluUtils.bytesToHexString(commandBytes), 8));
        if (!definition.isUnofficial()) {
            sb.append("  ");
        } else {
            sb.append(" *");
        }
        sb.append(ensureStringLength(commandName + " " + memoryString, 30));
        sb.append("  ");
        sb.append("A:").append(AluUtils.byteToHexString(getA())).append(" ");
        sb.append("X:").append(AluUtils.byteToHexString(getX())).append(" ");
        sb.append("Y:").append(AluUtils.byteToHexString(getY())).append(" ");
        sb.append("P:").append(AluUtils.byteToHexString(getAluFlags().getFlagByte())).append(" ");
        sb.append("SP:").append(AluUtils.byteToHexString((byte) getS()));
        return sb.toString().toUpperCase();
    }

    private String ensureStringLength(String string, int length) {
        if (string.length() >= length) {
            return string;
        }
        int difference = length - string.length();
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        for (int i = 0; i < difference; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private void fillArrayFromMemory(byte[] array, int offset) {
        for (int i = 0; i < array.length; i++) {
            byte value = memory.getValue(offset + i);
            array[i] = value;
        }
    }
}
