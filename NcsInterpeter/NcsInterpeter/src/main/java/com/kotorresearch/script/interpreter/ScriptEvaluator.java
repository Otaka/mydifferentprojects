package com.kotorresearch.script.interpreter;

import com.kotorresearch.script.utils.ByteArrayUtils;
import com.kotorresearch.script.data.ScriptFunction;
import com.kotorresearch.script.data.NwnVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.kotorresearch.script.utils.ByteArrayUtils.*;
import com.kotorresearch.script.data.NwnAction;
import java.util.Stack;

/**
 * @author Dmitry
 */
public class ScriptEvaluator {

    private String scriptName;
    private byte[] buffer;
    private int ip = 0;
    private byte[] stack = new byte[10 * 1024];//10kb
    private int sp = 0;
    private int bp = 0;
    private Map<Integer, String> stringsPool = new HashMap<>();
    private Map<String, Integer> stringsToIntegerPool = new HashMap<>();
    private int stringPoolNewIndex = 0;
    private List<Integer> returnAddresses = new ArrayList<>();
    private List<Integer> savedBps = new ArrayList<>();
    private FunctionsManager functionsManager;
    private Stack<NwnAction> queuedActionsWaitingForReceiverFunction = new Stack<>();

    public ScriptEvaluator(byte[] buffer, String name, FunctionsManager functionsManager) {
        this(buffer, name, functionsManager, false);
    }
    
    public ScriptEvaluator(byte[] buffer, String name, FunctionsManager functionsManager, boolean skipInitialization) {
        this.functionsManager = functionsManager;
        this.scriptName = name;
        if (!skipInitialization) {
            String magicSequence = getStringFromByteBuffer(buffer, 0, 4);
            String version = getStringFromByteBuffer(buffer, 4, 4);
            if (!magicSequence.equals("NCS ")) {
                throw new IllegalArgumentException("Script [" + name + "] should have magic sequence \"NCS \" but it has \"" + magicSequence + "\"");
            }
            if (!version.equals("V1.0")) {
                throw new IllegalArgumentException("Script [" + name + "] should have version \"V1.0\" but it has \"" + version + "\"");
            }
        }

        this.buffer = buffer;
        ip += 8;
    }

    public void evaluate() {
        runInstructions();
    }

    public int getSp() {
        return sp;
    }

    public int getBp() {
        return bp;
    }

    public int getIp() {
        return ip;
    }

    private void runInstructions() {
        int offset;
        int count;
        int value1;
        int value2;
        int type;
        while (true) {
            int savedIp = ip;
            int opcode = buffer[ip++];
            //Integer.toHexString(opcode);
            switch (opcode) {
                case CPDOWNSP://////////////
                    //Integer.toHexString(savedIp);
                    //Integer.toHexString(ip);
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    count = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    System.arraycopy(stack, sp - count, stack, sp + offset, count);
                    //dumpStack();
                    break;
                case RSADD:////////
                    ip++;
                    //RSADDLOC is also 4 bytes length, because it is handle to location in engine
                    sp += 4;
                    break;
                case CPTOPSP:////////////////
                    //dumpStack();
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    count = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    System.arraycopy(stack, sp + offset, stack, sp, count);
                    sp += count;
                    break;
                case CONST:
                    type = buffer[ip++] & 0xFF;
                    if (type != TYPE_STRING) {///////////////////
                        int value = getIntFromByteBuffer(buffer, ip);
                        ip += 4;
                        putIntToByteBuffer(stack, value, sp);
                        sp += 4;//dumpStack();
                    } else {
                        //CONSTS
                        /////////////////////////////
                        int length = getShortFromByteBuffer(buffer, ip);
                        ip += 2;
                        String string = getStringFromByteBuffer(buffer, ip, length);
                        ip += length;
                        int stringPoolIndex = putStringToPool(string);
                        putIntToByteBuffer(stack, stringPoolIndex, sp);
                        sp += 4;
                    }

                    break;
                case ACTION://///////////////
                    invokeAction();
                    //dumpStack();
                    break;
                case LOGANDII:
                case LOGORII:
                case INCORII:
                case EXCORII:
                case BOOLANDII:
                    //Integer.toHexString(savedIp);
                    ip++;//type;
                    value2 = getIntFromByteBuffer(stack, sp - 4);
                    value1 = getIntFromByteBuffer(stack, sp - 8);
                    int result = 0;
                    switch (opcode) {
                        case LOGANDII:///////////////////////////////
                            boolean v1 = value1 != 0;
                            boolean v2 = value2 != 0;
                            if (v1 && v2) {
                                result = 1;
                            } else {
                                result = 0;
                            }

                            break;
                        case LOGORII:///////////////////////////////////
                            boolean booleanv1 = value1 != 0;
                            boolean booleanv2 = value2 != 0;
                            if (booleanv1 || booleanv2) {
                                result = 1;
                            } else {
                                result = 0;
                            }
                            break;
                        case INCORII:///////////////////////////////////
                            result = value1 | value2;
                            break;
                        case EXCORII:////////////////////////////////////
                            result = value1 ^ value2;
                            break;
                        case BOOLANDII:///////////////////////
                            result = value1 & value2;
                            break;
                    }

                    putIntToByteBuffer(stack, result, sp - 8);
                    sp -= 4;
                    break;
                case EQUAL:///////////////////////////////
                case NEQUAL:////////////////////////////
                    type = getByteFromByteBuffer(buffer, ip++);
                    if (type == 0x24) {/////////////////////////
                        //compare structures

                        int sizeOfStructure = getShortFromByteBuffer(buffer, ip);
                        ip += 2;
                        int structure1Offset = sp - sizeOfStructure;
                        int structure2Offset = sp - (sizeOfStructure * 2);
                        sp -= sizeOfStructure * 2;
                        result = 1;
                        for (int i = 0; i < sizeOfStructure; i++) {
                            if (stack[structure1Offset + i] != stack[structure2Offset + i]) {
                                result = 0;
                                break;
                            }
                        }

                        if (opcode == NEQUAL) {
                            result = result == 0 ? 1 : 0;
                        }
                        putIntToByteBuffer(stack, result, sp);
                        sp += 4;
                    } else {//////////////////////////////////////////
                        value2 = getIntFromByteBuffer(stack, sp - 4);
                        value1 = getIntFromByteBuffer(stack, sp - 8);
                        switch (type) {
                            case 0x23://test string equality
                                //first check string indexes, if they are equal, then objects equal, in other case check string equality
                                if (value1 == value2) {
                                    result = 1;//////////////////////////////////////////
                                } else {
                                    String s1 = stringsPool.get(value1);
                                    String s2 = stringsPool.get(value2);
                                    result = s1.equals(s2) ? 1 : 0;/////////////////////////////
                                }
                                break;
                            default:
                                //EQUALII //////////////////////////////////////////////
                                //EQUALFF ///////////////////
                                //EQUALOO (by handles)///////////////////
                                result = value1 == value2 ? 1 : 0;
                                break;
                        }

                        if (opcode == NEQUAL) {
                            result = result == 0 ? 1 : 0;
                        }
                        putIntToByteBuffer(stack, result, sp - 8);
                        sp -= 4;
                    }
                    break;
                case GEQ:///////////////
                case GT:////////////////
                case LT://////////////
                case LEQ://///////////////
                    type = getByteFromByteBuffer(buffer, ip++);
                    value2 = getIntFromByteBuffer(stack, sp - 4);
                    value1 = getIntFromByteBuffer(stack, sp - 8);
                    result = 0;
                    if (type == 0x20) {//Integer-Integer
                        switch (opcode) {
                            case GEQ:
                                result = value1 >= value2 ? 1 : 0;
                                break;
                            case GT:
                                result = value1 > value2 ? 1 : 0;
                                break;
                            case LT:
                                result = value1 < value2 ? 1 : 0;
                                break;
                            case LEQ:
                                result = value1 <= value2 ? 1 : 0;
                                break;
                        }
                    } else if (type == 0x21) {//Float-Float   ////////////////////////
                        float f1 = Float.intBitsToFloat(value1);
                        float f2 = Float.intBitsToFloat(value2);
                        switch (opcode) {
                            case GEQ:
                                result = f1 >= f2 ? 1 : 0;
                                break;
                            case GT:
                                result = f1 > f2 ? 1 : 0;
                                break;
                            case LT:
                                result = f1 < f2 ? 1 : 0;
                                break;
                            case LEQ:
                                result = f1 <= f2 ? 1 : 0;
                                break;
                        }
                    } else {
                        throw new RuntimeException("Comparing opcode expect only type equal to 0x20 or 0x21, but received [" + ByteArrayUtils.hex(type, 1) + "]. It is bug");
                    }

                    putIntToByteBuffer(stack, result, sp - 8);
                    sp -= 4;
                    break;
                case SHLEFTII:////////////////
                case SHRIGHTII:///////////////
                case USHRIGHTII://////////////
                    ip++;//type
                    value2 = getIntFromByteBuffer(stack, sp - 4);
                    value1 = getIntFromByteBuffer(stack, sp - 8);
                    result = 0;
                    switch (opcode) {
                        case SHLEFTII:
                            result = value1 << value2;
                            break;
                        /*From here https://github.com/SkywingvL/nwn2dev-public/blob/master/NWNScriptLib/NWScriptVM.cpp#L2233
                            * The operation implemented here is actually a complex sequence that, if
                            *  the amount to be shifted is negative, involves both a front-loaded and
                            *  end-loaded negate built on top of a signed shift. */
                        case SHRIGHTII:
                            if (value2 < 0) {
                                value2 = -value2;
                                result = -(value1 >> value2);
                            } else {
                                result = value1 >> value2;
                            }
                            break;
                        case USHRIGHTII:
                            /* From here: https://github.com/SkywingvL/nwn2dev-public/blob/master/NWNScriptLib/NWScriptVM.cpp#L2272
                            * While this operator may have originally been intended to implement
                            *  an unsigned shift, it actually performs an arithmetic (signed) shift. */
                            result = value1 >> value2;
                            break;
                    }
                    putIntToByteBuffer(stack, result, sp - 8);
                    sp -= 4;
                    break;
                case ADD://////////////
                case SUB://////////////
                case DIV:////////////////
                case MUL:////////////////
                case MODII://///////////////
                    type = getByteFromByteBuffer(buffer, ip++);
                    value2 = getIntFromByteBuffer(stack, sp - 4);
                    value1 = getIntFromByteBuffer(stack, sp - 8);
                    result = 0;
                    boolean genericSaveResult = true;
                    if (type == 0x20) {//INTEGER-INTEGER
                        switch (opcode) {
                            case ADD:
                                result = value1 + value2;////////////
                                break;
                            case SUB:
                                result = value1 - value2;////////////
                                break;
                            case DIV:
                                result = value1 / value2;////////////
                                break;
                            case MUL:
                                result = value1 * value2;/////////////
                                break;
                            case MODII:
                                result = value1 % value2;///////////////
                                break;
                        }
                    } else if (type == 0x25) {//INTEGER-FLOAT  ////////////////
                        float f2 = Float.intBitsToFloat(value2);
                        switch (opcode) {
                            case ADD:
                                result = Float.floatToIntBits(value1 + f2);/////
                                break;
                            case SUB:
                                result = Float.floatToIntBits(value1 - f2);///////
                                break;
                            case DIV:
                                result = Float.floatToIntBits(value1 / f2);//////
                                break;
                            case MUL:
                                result = Float.floatToIntBits(value1 * f2);//////
                                break;
                        }
                    } else if (type == 0x26) {//FLOAT-INTEGER /////////
                        float f1 = Float.intBitsToFloat(value1);
                        switch (opcode) {
                            case ADD:
                                result = Float.floatToIntBits(f1 + value2);////////////
                                break;
                            case SUB:
                                result = Float.floatToIntBits(f1 - value2);/////////////
                                break;
                            case DIV:
                                result = Float.floatToIntBits(f1 / value2);////////////
                                break;
                            case MUL:
                                result = Float.floatToIntBits(f1 * value2);////////////
                                break;
                        }
                    } else if (type == 0x21) {//FLOAT-FLOAT
                        float f1 = Float.intBitsToFloat(value1);
                        float f2 = Float.intBitsToFloat(value2);
                        switch (opcode) {
                            case ADD:
                                result = Float.floatToIntBits(f1 + f2);//////////////
                                break;
                            case SUB:
                                result = Float.floatToIntBits(f1 - f2);//////////////
                                break;
                            case DIV:
                                result = Float.floatToIntBits(f1 / f2);//////////////
                                break;
                            case MUL:
                                result = Float.floatToIntBits(f1 * f2);//////////////
                                break;
                        }
                    } else if (type == 0x23) {//ADD STRING-STRING
                        if (opcode != ADD) {
                            throw new IllegalArgumentException("You can do STRING-STRING only with add, but found [" + ByteArrayUtils.hex(type, 1) + "]");
                        }

                        String v1 = stringsPool.get(value1);/////////////////////////
                        String v2 = stringsPool.get(value2);/////////////////////////
                        String resultString = v1 + v2;///////////////////////////////
                        result = putStringToPool(resultString);
                    } else if (type == 0x3A) {//ADD or SUB VECTORS///////////////////////////////////
                        //dumpStack();
                        sp -= 4;
                        float v2z = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float v2y = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float v2x = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float v1z = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float v1y = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float v1x = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));

                        if (opcode == ADD) {
                            v1x = v1x + v2x;
                            v1y = v1y + v2y;
                            v1z = v1z + v2z;
                        } else if (opcode == SUB) {
                            v1x = v1x - v2x;
                            v1y = v1y - v2y;
                            v1z = v1z - v2z;
                        }

                        putIntToByteBuffer(stack, Float.floatToIntBits(v1x), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(v1y), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(v1z), sp);
                        sp += 4;
                        genericSaveResult = false;
//dumpStack();
                    } else if (type == 0x3B) {//MUL or DIV Vector-Float
                        sp -= 4;
                        float fvalue = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));

                        sp -= 4;
                        float vz = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float vy = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float vx = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));

                        if (opcode == MUL) {
                            vx = vx * fvalue;
                            vy = vy * fvalue;
                            vz = vz * fvalue;
                        } else {//div
                            vx = vx / fvalue;
                            vy = vy / fvalue;
                            vz = vz / fvalue;
                        }

                        putIntToByteBuffer(stack, Float.floatToIntBits(vx), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(vy), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(vz), sp);
                        sp += 4;
                        genericSaveResult = false;
                        //dumpStack();
                    } else if (type == 0x3C) {//MUL Float-Vector
                        sp -= 4;
                        float vz = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float vy = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));
                        sp -= 4;
                        float vx = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));

                        sp -= 4;
                        float fvalue = Float.intBitsToFloat(getIntFromByteBuffer(stack, sp));

                        vx = vx * fvalue;
                        vy = vy * fvalue;
                        vz = vz * fvalue;

                        putIntToByteBuffer(stack, Float.floatToIntBits(vx), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(vy), sp);
                        sp += 4;
                        putIntToByteBuffer(stack, Float.floatToIntBits(vz), sp);
                        sp += 4;
                        genericSaveResult = false;
                        //dumpStack();
                    }

                    if (genericSaveResult) {
                        putIntToByteBuffer(stack, result, sp - 8);
                        sp -= 4;
                    }
                    break;
                case NEG://///////////////////////
                    type = getByteFromByteBuffer(buffer, ip++);
                    int value = getIntFromByteBuffer(stack, sp - 4);
                    result = 0;
                    switch (type) {
                        case 0x3:////////////////////////////////////////////
                            //Integer
                            result = value * -1;
                            break;
                        case 0x4:////////////////////////////////////////////
                            //float
                            result = Float.floatToIntBits(Float.intBitsToFloat(value) * -1);
                            break;
                        default:
                            throw new IllegalArgumentException("NEG expects only types [3 and 4] but found [" + ByteArrayUtils.hex(type, 1) + "]");
                    }

                    putIntToByteBuffer(stack, result, sp - 4);
                    break;
                case COMPI://///////////////////////////////////////////////////////////////
                    ip++;//type
                    value = getIntFromByteBuffer(stack, sp - 4);
                    putIntToByteBuffer(stack, ~value, sp - 4);
                    break;
                case MOVSP:////////////////////////////////////////////////////////
                    //dumpStack();
                    ip++;//type
                    value = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    sp += value;
                    if (sp < 0) {
                        throw new IllegalStateException("Stack is lesser than 0. Script [" + scriptName + "] IP:[" + ByteArrayUtils.hex(savedIp, 4) + "]");
                    }
                    break;
                case STORE_STATEALL:
                    throw new IllegalStateException("STORE_STATEALL is not implemented because it is obsoleted");
                case JMP:////////////////////////////////////////////////////
                    //Integer.toHexString(savedIp);
                    //Integer.toHexString(ip);
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip = savedIp + offset;
                    break;
                case JSR:///////??????????????partially. Test with custom functions, test reqursion
                    //Integer.toHexString(savedIp);
                    //Integer.toHexString(ip);
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    returnAddresses.add(ip);
                    ip = savedIp + offset;
                    break;
                case JZ:////////////////////////////////////////////////////////////////////
                    //Integer.toHexString(savedIp);
                    //Integer.toHexString(ip);
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, sp - 4);
                    sp -= 4;
                    if (value == 0) {
                        ip = savedIp + offset;
                    }
                    break;
                case JNZ:////////////////////////////////////////////////////////
                    //Integer.toHexString(savedIp);
                    //Integer.toHexString(ip);
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, sp - 4);
                    sp -= 4;
                    if (value != 0) {
                        ip = savedIp + offset;
                    }
                    break;
                case RETN:///////////////////////////////////////////////////////
                    ip++;//type
                    if (returnAddresses.isEmpty()) {
                        //it is the latest operation. exit
                        return;
                    }
                    int returnIp = returnAddresses.remove(returnAddresses.size() - 1);
                    ip = returnIp;
                    break;
                case DESTRUCT://///////////////////////
                    //dumpStack();
                    ip++;//type
                    int numberOfBytesToRemove = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    int offsetOfPreservedArea = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    int sizeOfPreservedArea = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    int blockStart = sp - numberOfBytesToRemove;
                    if (offsetOfPreservedArea != 0) {
                        System.arraycopy(stack, blockStart + offsetOfPreservedArea, stack, blockStart, sizeOfPreservedArea);
                    }
                    sp -= (numberOfBytesToRemove - sizeOfPreservedArea);
                    //throw new IllegalArgumentException("'Destroy' opcode is not implemented");
                    break;
                case NOTI:///////////////////////////////
                    ip++;//type
                    value = getIntFromByteBuffer(stack, sp - 4);
                    result = value == 0 ? 1 : 0;
                    putIntToByteBuffer(stack, result, sp - 4);
                    break;
                case DECISP://///////////////////////////////////////////
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, sp + offset);
                    value--;
                    putIntToByteBuffer(stack, value, sp + offset);
                    break;
                case INCISP:///////////////////////////////////////////
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, sp + offset);
                    value++;
                    putIntToByteBuffer(stack, value, sp + offset);
                    break;
                case CPDOWNBP://///////////////////////////////
                    //dumpStack();
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    count = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    System.arraycopy(stack, sp - count, stack, bp + offset, count);
                    break;
                case CPTOPBP://///////////////////////////////////////////
                    //dumpStack();
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    count = getShortFromByteBuffer(buffer, ip);
                    ip += 2;
                    System.arraycopy(stack, bp + offset, stack, sp, count);
                    sp += count;
                    break;
                case DECIBP://///////////////////////////////////////////
                    //dumpStack();
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, bp + offset);
                    value--;
                    putIntToByteBuffer(stack, value, bp + offset);
                    break;
                case INCIBP:////////////////////////////////////
                    //dumpStack();
                    ip++;//type
                    offset = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    value = getIntFromByteBuffer(stack, bp + offset);
                    value++;
                    putIntToByteBuffer(stack, value, bp + offset);
                    break;
                case SAVEBP:////////////////////
                    ip++;//type
                    savedBps.add(bp);
                    bp = sp;
                    break;
                case RESTOREBP:////////////////////////
                    ip++;//type
                    bp = savedBps.remove(savedBps.size() - 1);
                    break;
                case STORE_STATE:
                    storeState(savedIp);
                    break;
                case NOP:
                    ip++;//type
                    //nothing to implement
                    break;
                case T://
                    int scriptLength = getIntFromByteBuffer(buffer, ip);
                    ip += 4;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown opcode [" + ByteArrayUtils.hex(opcode, 1) + "]");
            }
        }
    }

    private void storeState(int originalIp) {
        int offsetToCode = buffer[ip] & 0xFF;
        ip++;
        int sizeOfStackFromBp = getIntFromByteBuffer(buffer, ip);
        ip += 4;
        int sizeOfStackFromSp = getIntFromByteBuffer(buffer, ip);
        ip += 4;
        int closureIp = originalIp + offsetToCode;
        //ByteArrayUtils.hex(closureIp, 4);
        ScriptEvaluator closureEvaluator = new ScriptEvaluator(buffer, scriptName, functionsManager,true);
        closureEvaluator.ip = closureIp;
        closureEvaluator.stringsPool = new HashMap<>(stringsPool);
        closureEvaluator.stringPoolNewIndex = stringPoolNewIndex;
        closureEvaluator.bp = sizeOfStackFromBp;
        closureEvaluator.sp = sizeOfStackFromBp + sizeOfStackFromSp;
        int stackPosition = 0;
        if (sizeOfStackFromBp != 0) {
            System.arraycopy(stack, bp - sizeOfStackFromBp, closureEvaluator.stack, 0, sizeOfStackFromBp);
            stackPosition = sizeOfStackFromBp;
        }

        if (sizeOfStackFromSp != 0) {
            System.arraycopy(stack, sp - sizeOfStackFromSp, closureEvaluator.stack, stackPosition, sizeOfStackFromSp);
        }

        NwnAction nwnAction = new NwnAction(this, closureEvaluator);
        queuedActionsWaitingForReceiverFunction.add(nwnAction);
    }

    private void invokeAction() throws RuntimeException {
        ip++;//type
        int functionIndex = getShortFromByteBuffer(buffer, ip);
        ip += 2;
        int argsCount = getByteFromByteBuffer(buffer, ip);
        ip++;
        ScriptFunction function = functionsManager.getFunctionByIndex(functionIndex);
        if (function == null) {
            throw new RuntimeException("Cannot find action with id [" + ByteArrayUtils.hex(functionIndex, 2) + "] called in script [" + scriptName + "]");
        }

        //fill arguments list
        Class[] functionArguments = function.getArguments();
        Object[] args = new Object[functionArguments.length];
        if (argsCount != args.length) {
            throw new RuntimeException("Action [" + function.getMethodToCall().getName() + "] in script [" + scriptName + "] at ip [" + (ip - 4) + "] has [" + args.length + "] but in script provided [" + argsCount + "] arguments");
        }

        for (int i = 0; i < functionArguments.length; i++) {
            Class argument = functionArguments[i];
            if (argument == int.class) {
                sp -= 4;
                int value = getIntFromByteBuffer(stack, sp);
                args[i] = value;
            } else if (argument == short.class) {
                sp -= 2;
                short value = (short) getShortFromByteBuffer(stack, sp);
                args[i] = value;
            } else if (argument == byte.class) {
                sp -= 1;
                byte value = (byte) getByteFromByteBuffer(stack, sp);
                args[i] = value;
            } else if (argument == float.class) {
                sp -= 4;
                int value = getIntFromByteBuffer(stack, sp);
                float floatValue = Float.intBitsToFloat(value);
                args[i] = floatValue;
            } else if (argument == String.class) {
                sp -= 4;
                int stringIndex = getIntFromByteBuffer(stack, sp);
                String stringValue = stringsPool.get(stringIndex);
                args[i] = stringValue;
            } else if (argument == NwnAction.class) {
                //NwnAction is pushed to this stack because in document about the commands
                //http://www.nynaeve.net/Skywing/nwn2/Documentation/ncs.html
                //written that:"The value of SP remains unchanged."
                NwnAction action = queuedActionsWaitingForReceiverFunction.pop();
                args[i] = action;
            } else {
                throw new RuntimeException("Action function [" + function.getMethodToCall().getName() + "] has unimplemented argument [" + argument.getSimpleName() + "]");
            }
        }

        //execute method
        Object result;
        try {
            result = function.getMethodToCall().invoke(function.getOwner(), args);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error while execution script action [" + function.getMethodToCall() + "]", ex);
        }

        //process return value
        Class returnType = function.getMethodToCall().getReturnType();
        if (returnType == void.class) {
            //nothing to do
        } else if (returnType == int.class) {
            int value = (int) result;
            putIntToByteBuffer(stack, value, sp);
            sp += 4;
        } else if (returnType == short.class) {
            short value = (short) result;
            putShortToByteBuffer(stack, value, sp);
            sp += 2;
        } else if (returnType == byte.class) {
            byte value = (byte) result;
            stack[sp] = value;
            sp++;
        } else if (returnType == float.class) {
            float value = (float) result;
            int convertedValue = Float.floatToIntBits(value);
            putIntToByteBuffer(stack, convertedValue, sp);
            sp += 4;
        } else if (returnType == String.class) {
            int index = putStringToPool((String) result);
            putIntToByteBuffer(stack, index, sp);
            sp += 4;
        } else if (returnType == NwnVector.class) {
            NwnVector vector = (NwnVector) result;
            putIntToByteBuffer(stack, Float.floatToIntBits(vector.x), sp);
            sp += 4;
            putIntToByteBuffer(stack, Float.floatToIntBits(vector.y), sp);
            sp += 4;
            putIntToByteBuffer(stack, Float.floatToIntBits(vector.z), sp);
            sp += 4;
        } else {
            throw new RuntimeException("Action function [" + function.getMethodToCall().getName() + "] has unimplemented return type [" + returnType.getSimpleName() + "]");
        }
    }

    public void dumpStack() {
        System.out.println("Stack dump");
        int spIntsCount = sp / 4;
        if (sp % 4 != 0) {
            System.out.println("Stack is not aligned to 4 byte. Diff (" + (sp % 4) + ")");
        }
        for (int i = 0; i < spIntsCount; i++) {
            int value = getIntFromByteBuffer(stack, i * 4);
            System.out.print(ByteArrayUtils.hex(value, 4));
            System.out.println("    " + Float.intBitsToFloat(value));
        }

        System.out.println("-------------------------------");
    }

    private int putStringToPool(String val) {
        Integer foundStringIndex = stringsToIntegerPool.get(val);
        if (foundStringIndex != null) {
            return foundStringIndex;
        }

        int newIndex = stringPoolNewIndex++;
        stringsPool.put(newIndex, val);
        stringsToIntegerPool.put(val, newIndex);
        return newIndex;
    }

    private final static int CPDOWNSP = 0x01;
    private final static int RSADD = 0x02;
    private final static int CPTOPSP = 0x03;
    private final static int CONST = 0x04;
    private final static int ACTION = 0x05;
    private final static int LOGANDII = 0x06;
    private final static int LOGORII = 0x07;
    private final static int INCORII = 0x08;
    private final static int EXCORII = 0x09;/////////////////////
    private final static int BOOLANDII = 0x0A;////////////////////
    private final static int EQUAL = 0x0B;
    private final static int NEQUAL = 0x0C;
    private final static int GEQ = 0x0D;
    private final static int GT = 0x0E;
    private final static int LT = 0x0F;
    private final static int LEQ = 0x10;
    private final static int SHLEFTII = 0x11;
    private final static int SHRIGHTII = 0x12;//////////////////
    private final static int USHRIGHTII = 0x13;/////////////////
    private final static int ADD = 0x14;
    private final static int SUB = 0x15;
    private final static int MUL = 0x16;
    private final static int DIV = 0x17;
    private final static int MODII = 0x18;
    private final static int NEG = 0x19;
    private final static int COMPI = 0x1A;////////////////////
    private final static int MOVSP = 0x1B;
    private final static int STORE_STATEALL = 0x1C;
    private final static int JMP = 0x1D;
    private final static int JSR = 0x1E;
    private final static int JZ = 0x1F;
    private final static int RETN = 0x20;
    private final static int DESTRUCT = 0x21;
    private final static int NOTI = 0x22;
    private final static int DECISP = 0x23;
    private final static int INCISP = 0x24;
    private final static int JNZ = 0x25;
    private final static int CPDOWNBP = 0x26;
    private final static int CPTOPBP = 0x27;
    private final static int DECIBP = 0x28;/////////////
    private final static int INCIBP = 0x29;//////////////
    private final static int SAVEBP = 0x2A;
    private final static int RESTOREBP = 0x2B;
    private final static int STORE_STATE = 0x2C;
    private final static int NOP = 0x2D;
    private final static int T = 0x42;

    private final static int TYPE_STRING = 5;
}
