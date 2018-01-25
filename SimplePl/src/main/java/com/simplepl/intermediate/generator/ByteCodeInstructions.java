package com.simplepl.intermediate.generator;

/**
 * @author sad
 */
public class ByteCodeInstructions {

    public static short OP_NOP = 0;

    public static short OP_ASSIGN_I8_V_LIT =  1;
    public static short OP_ASSIGN_I8_V_V =    2;
    public static short OP_ASSIGN_I16_V_LIT = 3;
    public static short OP_ASSIGN_I16_V_V = 4;
    public static short OP_ASSIGN_I32_V_L = 5;
    public static short OP_ASSIGN_I32_V_V = 6;
    public static short OP_ASSIGN_I64_V_L = 7;
    public static short OP_ASSIGN_I64_V_V = 8;
    public static short OP_ASSIGN_F32_V_L = 9;
    public static short OP_ASSIGN_F32_V_V = 10;
    public static short OP_ASSIGN_F64_V_L = 11;
    public static short OP_ASSIGN_F64_V_V = 12;

    public static short OP_ADD_I32_V_L = 13;
    public static short OP_ADD_I32_V_V = 14;
    public static short OP_ADD_I16_V_L = 15;
    public static short OP_ADD_I16_V_V = 16;
    public static short OP_ADD_I8_V_L =  17;
    public static short OP_ADD_I8_V_V =  18;
    public static short OP_ADD_I64_V_L = 19;
    public static short OP_ADD_I64_V_V = 20;
    public static short OP_ADD_F64_V_L = 21;
    public static short OP_ADD_F64_V_V = 22;
    public static short OP_ADD_F32_V_L = 23;
    public static short OP_ADD_F32_V_V = 24;

    public static short OP_CMP_I32_V_L = 25;
    public static short OP_CMP_I32_V_V = 26;
    public static short OP_CMP_I16_V_L = 27;
    public static short OP_CMP_I16_V_V = 28;
    public static short OP_CMP_I8_V_L =  29;
    public static short OP_CMP_I8_V_V =  30;
    public static short OP_CMP_I64_V_L = 31;
    public static short OP_CMP_I64_V_V = 32;
    public static short OP_CMP_F64_V_L = 33;
    public static short OP_CMP_F64_V_V = 34;
    public static short OP_CMP_F32_V_L = 35;
    public static short OP_CMP_F32_V_V = 36;

    public static short OP_MUL_I32_V_L = 37;
    public static short OP_MUL_I32_V_V = 38;
    public static short OP_MUL_I16_V_L = 39;
    public static short OP_MUL_I16_V_V = 40;
    public static short OP_MUL_I8_V_L =  41;
    public static short OP_MUL_I8_V_V =  42;
    public static short OP_MUL_I64_V_L = 43;
    public static short OP_MUL_I64_V_V = 44;
    public static short OP_MUL_F64_V_L = 45;
    public static short OP_MUL_F64_V_V = 46;
    public static short OP_MUL_F32_V_L = 47;
    public static short OP_MUL_F32_V_V = 48;
    
    public static short OP_DIV_I32_V_L = 49;
    public static short OP_DIV_I32_V_V = 50;
    public static short OP_DIV_I16_V_L = 51;
    public static short OP_DIV_I16_V_V = 52;
    public static short OP_DIV_I8_V_L =  53;
    public static short OP_DIV_I8_V_V =  54;
    public static short OP_DIV_I64_V_L = 55;
    public static short OP_DIV_I64_V_V = 56;
    public static short OP_DIV_F64_V_L = 57;
    public static short OP_DIV_F64_V_V = 58;
    public static short OP_DIV_F32_V_L = 47;
    public static short OP_DIV_F32_V_V = 48;

}
