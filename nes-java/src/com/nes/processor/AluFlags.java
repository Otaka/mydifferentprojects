package com.nes.processor;

/**
 * @author sad
 */
public class AluFlags {

    private boolean carry = false;
    private boolean zero = false;
    private boolean interrupt = true;
    private boolean decimal = false;
    private boolean breakFlag = false;
    private boolean l = true;//wtf?
    private boolean overflow = false;
    private boolean negative = false;

    public boolean isCarry() {
        return carry;
    }

    public void setCarry(boolean carry) {
        this.carry = carry;
    }

    public boolean isZero() {
        return zero;
    }

    public void setZero(boolean zero) {
        this.zero = zero;
    }

    public boolean isBreakFlag() {
        return breakFlag;
    }

    public void setBreakFlag(boolean breakFlag) {
        this.breakFlag = breakFlag;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    public boolean isDecimal() {
        return decimal;
    }

    public void setDecimal(boolean decimal) {
        this.decimal = decimal;
    }

    public boolean isL() {
        return l;
    }

    public void setL(boolean l) {
        //this.l = l;
    }

    public boolean isOverflow() {
        return overflow;
    }

    public void setOverflow(boolean vFlag) {
        this.overflow = vFlag;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public byte getFlagByte() {
        byte b = 0;
        b = AluUtils.setBit(b, 0, isCarry());
        b = AluUtils.setBit(b, 1, isZero());
        b = AluUtils.setBit(b, 2, isInterrupt());
        b = AluUtils.setBit(b, 3, isDecimal());
        b = AluUtils.setBit(b, 4, isBreakFlag());
        b = AluUtils.setBit(b, 5, isL());
        b = AluUtils.setBit(b, 6, isOverflow());
        b = AluUtils.setBit(b, 7, isNegative());
        return b;
    }

    public void setFlagByte(byte flag) {
        int i = 0;
        setCarry(AluUtils.isBit(flag, i++));
        setZero(AluUtils.isBit(flag, i++));
        setInterrupt(AluUtils.isBit(flag, i++));
        setDecimal(AluUtils.isBit(flag, i++));
        setBreakFlag(AluUtils.isBit(flag, i++));
        setL(AluUtils.isBit(flag, i++));
        setOverflow(AluUtils.isBit(flag, i++));
        setNegative(AluUtils.isBit(flag, i++));
    }
}
