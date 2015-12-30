package com.nes.processor.commands;

/**
 * @author Dmitry
 */
public class Commands {

    /**
     * ADd with Carry
     */
    public static final byte ADC_IMM = 0x69;
    public static final byte ADC_ZP = 0x65;
    public static final byte ADC_ZP_X = 0x75;
    public static final byte ADC_ABS = 0x6d;
    public static final byte ADC_ABS_X = 0x7d;
    public static final byte ADC_ABS_Y = 0x79;
    public static final byte ADC_IND_X = 0x61;
    public static final byte ADC_IND_Y = 0x71;
    /**
     * bitwise AND with accumulator
     */
    public static final byte AND_IMM = 0x29;
    public static final byte AND_ABS = 0x2d;
    public static final byte AND_ABS_X = 0x3d;
    public static final byte AND_ABS_Y = 0x39;
    public static final byte AND_IND_X = 0x21;
    public static final byte AND_IND_Y = 0x31;
    public static final byte AND_ZP = 0x25;
    public static final byte AND_ZP_X = 0x35;
    /**
     * Arithmetic Shift Left
     */
    public static final byte ASL_ACC = 0x0A;
    public static final byte ASL_ABS = 0x0E;
    public static final byte ASL_ABS_X = 0x1E;
    public static final byte ASL_ZP = 0x06;
    public static final byte ASL_ZP_X = 0x16;
    /**
     * Branch if Carry Clear C = 0
     */
    public static final byte BCC_REL = (byte) 0x90;
    /**
     * Branch if Carry Set C = 1
     */
    public static final byte BCS_REL = (byte) 0xB0;
    /**
     * Branch if Equal Z = 1
     */
    public static final byte BEQ_REL = (byte) 0xF0;
    /**
     * test BITs
     */
    public static final byte BIT_ZP = (byte) 0x24;
    public static final byte BIT_ABS = (byte) 0x2C;
    /**
     * Branch on N = 1
     */
    public static final byte BMI_REL = (byte) 0x30;
    /**
     * Branch on Z = 0
     */
    public static final byte BNE_REL = (byte) 0xD0;
    /**
     * Branch on N = 0
     */
    public static final byte BPL_REL = (byte) 0x10;
    /**
     * Force Interrupt
     */
    public static final byte BRK = (byte) 0x00;
    /**
     * Branch on V=1
     */
    public static final byte BVS_REL = (byte) 0x70;
    /**
     * Branch on V=0
     */
    public static final byte BVC_REL = (byte) 0x50;
    /**
     * Set the carry flag to zero
     */
    public static final byte CLC = (byte) 0x18;
    /**
     * Sets the decimal mode flag to zero.
     */
    public static final byte CLD = (byte) 0xD8;
    /**
     * Clear Interrupt Disable
     */
    public static final byte CLI = (byte) 0x58;
    /**
     * Clear Overflow Flag
     */
    public static final byte CLV = (byte) 0xB8;
    /**
     * Compare
     */
    public static final byte CMP_IMM = (byte) 0xC9;
    public static final byte CMP_ZP = (byte) 0xC5;
    public static final byte CMP_ZP_X = (byte) 0xD5;
    public static final byte CMP_ABS = (byte) 0xCD;
    public static final byte CMP_ABS_X = (byte) 0xDD;
    public static final byte CMP_ABS_Y = (byte) 0xD9;
    public static final byte CMP_IND_X = (byte) 0xC1;
    public static final byte CMP_IND_Y = (byte) 0xD1;
    /**
     * Compare X Register
     */
    public static final byte CPX_ABS = (byte) 0xEC;
    public static final byte CPX_IMM = (byte) 0xE0;
    public static final byte CPX_ZP = (byte) 0xE4;
    /**
     * Compare Y Register
     */
    public static final byte CPY_ABS = (byte) 0xCC;
    public static final byte CPY_IMM = (byte) 0xC0;
    public static final byte CPY_ZP = (byte) 0xC4;
    /**
     * Decrement Memory
     */
    public static final byte DEC_ZP = (byte) 0xC6;
    public static final byte DEC_ZP_X = (byte) 0xD6;
    public static final byte DEC_ABS = (byte) 0xCE;
    public static final byte DEC_ABS_X = (byte) 0xDE;
    /**
     * Decrement X Register
     */
    public static final byte DEX = (byte) 0xCA;
    /**
     * Decrement Y Register
     */
    public static final byte DEY = (byte) 0x88;
    /**
     * Exclusive OR
     */
    public static final byte EOR_IMM = (byte) 0x49;
    public static final byte EOR_ZP = (byte) 0x45;
    public static final byte EOR_ZP_X = (byte) 0x55;
    public static final byte EOR_ABS = (byte) 0x4D;
    public static final byte EOR_ABS_X = (byte) 0x5D;
    public static final byte EOR_ABS_Y = (byte) 0x59;
    public static final byte EOR_IND_X = (byte) 0x41;
    public static final byte EOR_IND_Y = (byte) 0x51;
    /**
     * Increment Memory
     */
    public static final byte INC_ZP = (byte) 0xE6;
    public static final byte INC_ZP_X = (byte) 0xF6;
    public static final byte INC_ABS = (byte) 0xEE;
    public static final byte INC_ABS_X = (byte) 0xFE;
    /**
     * Increment X Register
     */
    public static final byte INX = (byte) 0xE8;
    /**
     * Increment Y Register
     */
    public static final byte INY = (byte) 0xC8;
    /**
     * Jump
     */
    public static final byte JMP_ABS16 = (byte) 0x4C;
    public static final byte JMP_IND = (byte) 0x6C;///?????
    /**
     * Jump to Subroutine
     */
    public static final byte JSR_ABS16 = (byte) 0x20;
    /**
     * Load Accumulator
     */
    public static final byte LDA_IMM = (byte) 0xA9;
    public static final byte LDA_ZP = (byte) 0xA5;
    public static final byte LDA_ZP_X = (byte) 0xB5;
    public static final byte LDA_ABS = (byte) 0xAD;
    public static final byte LDA_ABS_X = (byte) 0xBD;
    public static final byte LDA_ABS_Y = (byte) 0xB9;
    public static final byte LDA_IND_X = (byte) 0xA1;
    public static final byte LDA_IND_Y = (byte) 0xB1;
    /**
     * Load X Register
     */
    public static final byte LDX_IMM = (byte) 0xA2;
    public static final byte LDX_ABS = (byte) 0xAE;
    public static final byte LDX_ABS_Y = (byte) 0xBE;
    public static final byte LDX_ZP = (byte) 0xA6;
    public static final byte LDX_ZP_Y = (byte) 0xB6;
    /**
     * Load Y Register
     */
    public static final byte LDY_IMM = (byte) 0xA0;
    public static final byte LDY_ABS = (byte) 0xAC;
    public static final byte LDY_ABS_X = (byte) 0xBC;
    public static final byte LDY_ZP = (byte) 0xA4;
    public static final byte LDY_ZP_X = (byte) 0xB4;
    /**
     * Logical Shift Right
     */
    public static final byte LSR_ACC = (byte) 0x4A;
    public static final byte LSR_ZP = (byte) 0x46;
    public static final byte LSR_ZP_X = (byte) 0x56;
    public static final byte LSR_ABS = (byte) 0x4E;
    public static final byte LSR_ABS_X = (byte) 0x5E;
    /**
     * No Operation
     */
    public static final byte NOP = (byte) 0xEA;
    /*
     * Unofficial nop operation
     */
    public static final byte NOP_ZP = (byte) 0x04;
    public static final byte NOP_ZP$1 = (byte) 0x44;
    public static final byte NOP_ZP$2 = (byte) 0x64;
    public static final byte NOP_ABS = (byte) 0x0C;
    public static final byte NOP_ZP_X = (byte) 0x14;
    public static final byte NOP_ZP_X$1 = (byte) 0x34;
    public static final byte NOP_ZP_X$2 = (byte) 0x54;
    public static final byte NOP_ZP_X$3 = (byte) 0x74;
    public static final byte NOP_ZP_X$4 = (byte) 0xD4;
    public static final byte NOP_ZP_X$5 = (byte) 0xF4;
    public static final byte NOP$1 = (byte) 0x1A;
    public static final byte NOP$2 = (byte) 0x3A;
    public static final byte NOP$3 = (byte) 0x5A;
    public static final byte NOP$4 = (byte) 0x7A;
    public static final byte NOP$5 = (byte) 0xDA;
    public static final byte NOP$6 = (byte) 0xFA;
    public static final byte NOP_IMM = (byte) 0x80;
    public static final byte NOP_IMM$1 = (byte) 0x82;
    public static final byte NOP_IMM$2 = (byte) 0xC2;
    public static final byte NOP_ABS_X$1 = (byte) 0x1C;
    public static final byte NOP_ABS_X$2 = (byte) 0x3C;
    public static final byte NOP_ABS_X$3 = (byte) 0x5C;
    public static final byte NOP_ABS_X$4 = (byte) 0x7C;
    public static final byte NOP_ABS_X$5 = (byte) 0xDC;
    public static final byte NOP_ABS_X$6 = (byte) 0xFC;
    /**
     * Logical Inclusive OR
     */
    public static final byte ORA_IMM = (byte) 0x09;
    public static final byte ORA_ZP = (byte) 0x05;
    public static final byte ORA_ZP_X = (byte) 0x15;
    public static final byte ORA_ABS = (byte) 0x0D;
    public static final byte ORA_ABS_X = (byte) 0x1D;
    public static final byte ORA_ABS_Y = (byte) 0x19;
    public static final byte ORA_IND_X = (byte) 0x01;
    public static final byte ORA_IND_Y = (byte) 0x11;
    /**
     * Push Accumulator
     */
    public static final byte PHA = (byte) 0x48;
    /**
     * Push Processor Status
     */
    public static final byte PHP = (byte) 0x08;
    /**
     * Pull Accumulator
     */
    public static final byte PLA = (byte) 0x68;
    /**
     * Pull Processor Status
     */
    public static final byte PLP = (byte) 0x28;
    /**
     * Rotate Left
     */
    public static final byte ROL_ACC = (byte) 0x2A;
    public static final byte ROL_ABS = (byte) 0x2E;
    public static final byte ROL_ABS_X = (byte) 0x3E;
    public static final byte ROL_ZP = (byte) 0x26;
    public static final byte ROL_ZP_X = (byte) 0x36;
    /**
     * Rotate Right
     */
    public static final byte ROR_ACC = (byte) 0x6A;
    public static final byte ROR_ZP = (byte) 0x66;
    public static final byte ROR_ZP_X = (byte) 0x76;
    public static final byte ROR_ABS = (byte) 0x6E;
    public static final byte ROR_ABS_X = (byte) 0x7E;
    /**
     * Return from Interrupt
     */
    public static final byte RTI = (byte) 0x40;
    /**
     * Return from Subroutine
     */
    public static final byte RTS = (byte) 0x60;
    /**
     * Subtract with Carry
     */
    public static final byte SBC_IMM = (byte) 0xE9;
    public static final byte SBC_ZP = (byte) 0xE5;
    public static final byte SBC_ZP_X = (byte) 0xF5;
    public static final byte SBC_ABS = (byte) 0xED;
    public static final byte SBC_ABS_X = (byte) 0xFD;
    public static final byte SBC_ABS_Y = (byte) 0xF9;
    public static final byte SBC_IND_X = (byte) 0xE1;
    public static final byte SBC_IND_Y = (byte) 0xF1;
    /**
     * Set Carry Flag
     */
    public static final byte SEC = (byte) 0x38;
    /**
     * Set Decimal Flag
     */
    public static final byte SED = (byte) 0xF8;
    /**
     * Set Interrupt Disable
     */
    public static final byte SEI = (byte) 0x78;
    /**
     * Store Accumulator
     */
    public static final byte STA_ABS = (byte) 0x8D;
    public static final byte STA_ABS_X = (byte) 0x9D;
    public static final byte STA_ABS_Y = (byte) 0x99;
    public static final byte STA_IND_X = (byte) 0x81;
    public static final byte STA_IND_Y = (byte) 0x91;
    public static final byte STA_ZP = (byte) 0x85;
    public static final byte STA_ZP_X = (byte) 0x95;
    /**
     * Store X Register
     */
    public static final byte STX_ABS = (byte) 0x8E;
    public static final byte STX_ZP = (byte) 0x86;
    public static final byte STX_ZP_Y = (byte) 0x96;
    /**
     * Store Y Register
     */
    public static final byte STY_ABS = (byte) 0x8C;
    public static final byte STY_ZP = (byte) 0x84;
    public static final byte STY_ZP_X = (byte) 0x94;
    /**
     * Transfer Accumulator to X
     */
    public static final byte TAX = (byte) 0xAA;
    /**
     * Transfer Accumulator to Y
     */
    public static final byte TAY = (byte) 0xA8;
    /**
     * Transfer Stack Pointer to X
     */
    public static final byte TSX = (byte) 0xBA;
    /**
     * Transfer X to Accumulator
     */
    public static final byte TXA = (byte) 0x8A;
    /**
     * Transfer X to Stack Pointer
     */
    public static final byte TXS = (byte) 0x9A;
    /**
     * Transfer Y to Accumulator
     */
    public static final byte TYA = (byte) 0x98;
    /**
     * Unofficial
     */
    /**
     * Load accumulator and x from memory
     */
    public static final byte LAX_ZP = (byte) 0xA7;
    public static final byte LAX_ZP_Y = (byte) 0xB7;
    public static final byte LAX_ABS = (byte) 0xAF;
    public static final byte LAX_ABS_Y = (byte) 0xBF;
    public static final byte LAX_IND_X = (byte) 0xA3;
    public static final byte LAX_IND_Y = (byte) 0xB3;
    /**
     * AND X register with accumulator and store result in memory.
     */
    public static final byte SAX_ZP = (byte) 0x87;
    public static final byte SAX_ZP_Y = (byte) 0x97;
    public static final byte SAX_IND_X = (byte) 0x83;
    public static final byte SAX_ABS = (byte) 0x8F;
    /**
     * Opcode EB seems to work exactly like SBC immediate.  Takes 2 cycles.
     */
    public static final byte SBC_IMM$1 = (byte) 0xEB;
    /**
     * This opcode DECs the contents of a memory location and then CMPs the result with the A register.
     */
    public static final byte DCP_IND_X = (byte) 0xC3;
    public static final byte DCP_IND_Y = (byte) 0xD3;
    public static final byte DCP_ZP = (byte) 0xC7;
    public static final byte DCP_ZP_X = (byte) 0xD7;
    public static final byte DCP_ABS = (byte) 0xCF;
    public static final byte DCP_ABS_X = (byte) 0xDF;
    public static final byte DCP_ABS_Y = (byte) 0xDB;
    
    /**
     * This opcode INCs the contents of a memory location and then SBCs the result from the A register.
     */
    public static final byte ISB_ZP = (byte) 0xE7;
    public static final byte ISB_ZP_X = (byte) 0xF7;
    public static final byte ISB_ABS = (byte) 0xEF;
    public static final byte ISB_ABS_X = (byte) 0xFF;
    public static final byte ISB_ABS_Y = (byte) 0xFB;
    public static final byte ISB_IND_X = (byte) 0xE3;
    public static final byte ISB_IND_Y = (byte) 0xF3;
    
    /**
     * Shift left one bit in memory, then OR accumulator with memory.
     */
    public static final byte SLO_ZP = (byte) 0x07;
    public static final byte SLO_ZP_X = (byte) 0x17;
    public static final byte SLO_ABS = (byte) 0x0F;
    public static final byte SLO_ABS_X = (byte) 0x1F;
    public static final byte SLO_ABS_Y = (byte) 0x1B;
    public static final byte SLO_IND_X = (byte) 0x03;
    public static final byte SLO_IND_Y = (byte) 0x13;
    /**
     * ROLs the contents of a memory location and then ANDs the result with the accumulator.
     */
    public static final byte RLA_ZP = (byte) 0x27;
    public static final byte RLA_ZP_X = (byte) 0x37;
    public static final byte RLA_ABS = (byte) 0x2F;
    public static final byte RLA_ABS_X = (byte) 0x3F;
    public static final byte RLA_ABS_Y = (byte) 0x3B;
    public static final byte RLA_IND_X = (byte) 0x23;
    public static final byte RLA_IND_Y = (byte) 0x33;
    /**
     * Shift right one bit in memory, then EOR accumulator with memory.
     */
    public static final byte SRE_ZP = (byte) 0x47;
    public static final byte SRE_ZP_X = (byte) 0x57;
    public static final byte SRE_ABS = (byte) 0x4F;
    public static final byte SRE_ABS_X = (byte) 0x5F;
    public static final byte SRE_ABS_Y = (byte) 0x5B;
    public static final byte SRE_IND_X = (byte) 0x43;
    public static final byte SRE_IND_Y = (byte) 0x53;
    /**
     * Rotate one bit right in memory, then add memory to accumulator (with carry)
     */
    public static final byte RRA_ZP = (byte) 0x67;
    public static final byte RRA_ZP_X = (byte) 0x77;
    public static final byte RRA_ABS = (byte) 0x6F;
    public static final byte RRA_ABS_X = (byte) 0x7F;
    public static final byte RRA_ABS_Y = (byte) 0x7B;
    public static final byte RRA_IND_X = (byte) 0x63;
    public static final byte RRA_IND_Y = (byte) 0x73;
}

