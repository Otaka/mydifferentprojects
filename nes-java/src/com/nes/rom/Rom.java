package com.nes.rom;

/**
 * @author sad
 */
public class Rom {

    private int prgRomSize;
    private int vromSize;
    private int mirroringMode;
    private int battery_backed_ram;
    private boolean hasTrainer;
    private boolean hasFourScreenVram;
    private int mapper;
    private int vsSystemCartrige;
    private int prgRamSize;
    private int palOrNtsc;
    private int prgRomBankCount;
    private int VRomBankCount;
    private byte[][] romBanks;
    private byte[][] vromBanks;

    public int getPrgRomBankCount() {
        return prgRomBankCount;
    }

    public void setPrgRomBankCount(int prgRomBankCount) {
        this.prgRomBankCount = prgRomBankCount;
    }

    public int getVRomBankCount() {
        return VRomBankCount;
    }

    public void setChrRomBankCount(int VRomBankCount) {
        this.VRomBankCount = VRomBankCount;
    }

    public int getPrgRomSize() {
        return prgRomSize;
    }

    public void setPrgRomSize(int prgRomSize) {
        this.prgRomSize = prgRomSize;
    }

    public int getVromSize() {
        return vromSize;
    }

    public void setChrBankSize(int vromSize) {
        this.vromSize = vromSize;
    }

    public int getMirroringMode() {
        return mirroringMode;
    }

    public void setMirroringMode(int mirroringMode) {
        this.mirroringMode = mirroringMode;
    }

    public int getBattery_backed_ram() {
        return battery_backed_ram;
    }

    public void setBattery_backed_ram(int battery_backed_ram) {
        this.battery_backed_ram = battery_backed_ram;
    }

    public boolean getHasTrainer() {
        return hasTrainer;
    }

    public void setHasTrainer(boolean hasTrainer) {
        this.hasTrainer = hasTrainer;
    }

    public boolean getHasFourScreenVram() {
        return hasFourScreenVram;
    }

    public void setHasFourScreenVram(boolean hasFourScreenVram) {
        this.hasFourScreenVram = hasFourScreenVram;
    }

    public int getVsSystemCartrige() {
        return vsSystemCartrige;
    }

    public void setVsSystemCartrige(int vsSystemCartrige) {
        this.vsSystemCartrige = vsSystemCartrige;
    }

    public int getMapper() {
        return mapper;
    }

    public void setMapper(int mapper) {
        this.mapper = mapper;
    }

    public int getPrgRamSize() {
        return prgRamSize;
    }

    public void setPrgRamSize(int prgRamSize) {
        this.prgRamSize = prgRamSize;
    }

    public int getPalOrNtsc() {
        return palOrNtsc;
    }

    public void setPalOrNtsc(int palOrNtsc) {
        this.palOrNtsc = palOrNtsc;
    }

    public byte[][] getRomBanks() {
        return romBanks;
    }

    public void setPrgRomBanks(byte[][] romBanks) {
        this.romBanks = romBanks;
    }

    public byte[][] getVromBanks() {
        return vromBanks;
    }

    public void setChrRomBanks(byte[][] vromBanks) {
        this.vromBanks = vromBanks;
    }

    public byte readPrgRom(int value) {
        int pointer=value-0x8000;
        if(pointer>16384){
            pointer-=16384;
            return romBanks[1][pointer];
        }
        return romBanks[0][pointer];
    }

    public void writePrgRom(int value, byte b) {
    }

    public byte readExpansionRom(int value) {

        return 0;
    }

    public void writeExpansionRom(int value, byte b) {
    }
}
