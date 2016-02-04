package com.nwn.data.script;

/**
 * @author Dmitry
 */
public class ScriptManager {

    private int index = 0;
    private final NwStack stack;
    private byte[] buffer;

    public ScriptManager() {
        stack = new NwStack(300);
    }

    public void execute(NwScript script) {
        index = 0;
        buffer = script.getCommandArray();
        while (true) {
            byte opcode = getCommandByte();
            process(opcode);
        }
    }

    private byte getCommandByte() {
        byte opcode = buffer[index];
        index++;
        return opcode;
    }

    private int getCommandInt() {
        int result = buffer[index + 3] & 0xFF
                | (buffer[index + 2] & 0xFF) << 8
                | (buffer[index + 1] & 0xFF) << 16
                | (buffer[index + 0] & 0xFF) << 24;
        index += 4;
        return result;
    }

    private int getCommandShort() {
        int result = buffer[index + 1] & 0xFF
                | (buffer[index + 0] & 0xFF) << 8;
        index += 2;
        return result;
    }

    private void cpDownSp() {
        getCommandByte();//skip
        int address = getCommandInt();
        int count = getCommandShort();
        count /= 4;
        address /= 4;
        for (int i = 0; i < count; i++) {
            NwVariable sourceVariable = stack.getVariable(-i);
            NwVariable destVariable = stack.getVariable(-(address + i));
            destVariable.copyFrom(sourceVariable);
        }
    }

    private void rsAdd() {
        getCommandByte();//skip
        stack.incStack(1);
    }

    private void cpTopSp() {
        getCommandByte();//skip
        int address = getCommandInt();
        int count = getCommandShort();
        count /= 4;
        address /= 4;
        int sourceAddress = address + count;
        for (int i = 0; i < count; i++, sourceAddress--) {
            NwVariable sourceVariable = stack.getVariable(sourceAddress);
            NwVariable destVariable = stack.getVariable(i);
            destVariable.copyFrom(sourceVariable);
        }
    }

    private void constOpcode() {
        byte type = getCommandByte();
        switch (type) {
            case 0x03:
                int intValue = getCommandInt();
                stack.pushInt(intValue);
                break;
            case 0x04:
                float floatValue = Float.intBitsToFloat(getCommandInt());
                stack.pushFloat(floatValue);
                break;
            case 0x05:
                break;
            case 0x06:
                break;
        }
    }

    private void actionCall() {
    }

    private void logAndII() {
        index++;
        int value1 = stack.popInt();
        int value2 = stack.popInt();
        stack.pushInt((value1 & value2) == 0 ? 0 : 1);
    }

    private void logOrII() {
        index++;
        int value1 = stack.popInt();
        int value2 = stack.popInt();
        stack.pushInt((value1 | value2) == 0 ? 0 : 1);
    }

    private void incOrII() {
        index++;
        int value1 = stack.popInt();
        int value2 = stack.popInt();
        stack.pushInt(value1 | value2);
    }

    private void excOrII() {
        index++;
        int value1 = stack.popInt();
        int value2 = stack.popInt();
        stack.pushInt(value1 ^ value2);

    }

    private void boolAndII() {
        index++;
        int value1 = stack.popInt();
        int value2 = stack.popInt();
        stack.pushInt((value1 & value2) == 0 ? 0 : 1);
    }

    private void equal() {

    }

    private void nequal() {
    }

    private void geq() {
    }

    private void gt() {
    }

    private void lt() {
    }

    private void leq() {
    }

    private void shLeftII() {
    }

    private void shRightII() {
    }

    private void ushRightII() {
    }

    private void add() {
    }

    private void sub() {
    }

    private void mul() {
    }

    private void div() {
    }

    private void modII() {
    }

    private void neg() {
    }

    private void compI() {
    }

    private void movsp() {
    }

    private void storeStateAll() {
    }

    private void jmp() {
    }

    private void jsr() {
    }

    private void jz() {
    }

    private void retn() {
    }

    private void destruct() {
    }

    private void notI() {
    }

    private void decIsp() {
    }

    private void incIsp() {
    }

    private void jnz() {
    }

    private void cpDownBp() {
    }

    private void cpTopBp() {
    }

    private void decIBp() {
    }

    private void incIBp() {
    }

    private void saveBp() {
    }

    private void restoreBp() {
    }

    private void storeState() {
    }

    private void nop() {
    }

    private void process(byte opcode) {
        switch (opcode) {
            case 0x01:
                cpDownSp();
                break;
            case 0x02:
                rsAdd();
                break;
            case 0x03:
                cpTopSp();
                break;
            case 0x04:
                constOpcode();
                break;
            case 0x05:
                actionCall();
                break;
            case 0x06:
                logAndII();
                break;
            case 0x07:
                logOrII();
                break;
            case 0x08:
                incOrII();
                break;
            case 0x09:
                excOrII();
                break;
            case 0x0A:
                boolAndII();
                break;
            case 0x0B:
                equal();
                break;
            case 0x0C:
                nequal();
                break;
            case 0x0D:
                geq();
                break;
            case 0x0E:
                gt();
                break;
            case 0x0F:
                lt();
                break;
            case 0x10:
                leq();
                break;
            case 0x11:
                shLeftII();
                break;
            case 0x12:
                shRightII();
                break;
            case 0x13:
                ushRightII();
                break;
            case 0x14:
                add();
                break;
            case 0x15:
                sub();
                break;
            case 0x16:
                mul();
                break;
            case 0x17:
                div();
                break;
            case 0x18:
                modII();
                break;
            case 0x19:
                neg();
                break;
            case 0x1A:
                compI();
                break;
            case 0x1B:
                movsp();
                break;
            case 0x1C:
                storeStateAll();
                break;
            case 0x1D:
                jmp();
                break;
            case 0x1E:
                jsr();
                break;
            case 0x1F:
                jz();
                break;
            case 0x20:
                retn();
                break;
            case 0x21:
                destruct();
                break;
            case 0x22:
                notI();
                break;
            case 0x23:
                decIsp();
                break;
            case 0x24:
                incIsp();
                break;
            case 0x25:
                jnz();
                break;
            case 0x26:
                cpDownBp();
                break;
            case 0x27:
                cpTopBp();
                break;
            case 0x28:
                decIBp();
                break;
            case 0x29:
                incIBp();
                break;
            case 0x2A:
                saveBp();
                break;
            case 0x2B:
                restoreBp();
                break;
            case 0x2C:
                storeState();
                break;
            case 0x2D:
                nop();
                break;
        }
    }
}
