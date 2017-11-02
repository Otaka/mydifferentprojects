package com.nes.rom;

import com.nes.processor.AluUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sad
 */
public class RomReader {

    public Rom createRom(File file) {
        try {
            Rom rom = new Rom();
            int mapper;
            int prgRomBankCount;
            int prgRomSize;
            int chrRomBankCount;
            int chrRomSize;
            int mirroringMode;
            int hasBatteryBackedRam;
            int hasTrainer;
            int hasFourScreenVram;
            int prgRamSizeInBytes;
            int palOrNtsc;
            byte[][] prgRomBanks;
            byte[][] chrRomBanks;

            try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
                stream.mark((int) file.length());
                byte[] magicNumber = new byte[4];
                stream.read(magicNumber);
                AluUtils.assertBytesEquals(magicNumber, new byte[]{0x4e, 0x45, 0x53, 0x1A});
                mapper = 0;
                int prgRomBankSize = 16384;
                int chrRomBankSize = 8192;
                prgRomBankCount = AluUtils.unsignedByte((byte) stream.read());
                prgRomSize = prgRomBankCount * prgRomBankSize;
                chrRomBankCount = AluUtils.unsignedByte((byte) stream.read());
                chrRomSize = chrRomBankCount * chrRomBankSize;
                int flags = stream.read();
                mirroringMode = flags & 0x01;
                hasBatteryBackedRam = (flags >> 1) & 0x01;
                hasTrainer = (flags >> 2) & 0x01;
                if (hasTrainer == 1) {
                    throw new RuntimeException("Rom has header. Cannot load it.");//TODO
                }
                hasFourScreenVram = (flags >> 3) & 0x01;
                int lowBitsMapperType = (flags >> 4) & 0x07;
                mapper = lowBitsMapperType;
                flags = stream.read();
                int isVsSystemCartridge = flags & 0x01;
                int highBitsMapperType = (flags >> 4) & 0x07;
                mapper = mapper | highBitsMapperType << 4;
                prgRamSizeInBytes = AluUtils.unsignedByte((byte) stream.read());
                if (prgRamSizeInBytes == 0) {
                    prgRamSizeInBytes = 1;
                }
                flags = stream.read();
                palOrNtsc = flags & 0x01;
                stream.reset();
                stream.skip(16);
                prgRomBanks = readBank(stream, prgRomBankCount, prgRomBankSize);
                chrRomBanks = readBank(stream, chrRomBankCount, chrRomSize);
            }
            rom.setMapper(mapper);
            rom.setPrgRomBankCount(prgRomBankCount);
            rom.setChrRomBankCount(chrRomBankCount);
            rom.setPrgRomSize(prgRomSize);
            rom.setChrBankSize(chrRomSize);
            rom.setMirroringMode(mirroringMode);
            rom.setBattery_backed_ram(hasBatteryBackedRam);
            rom.setHasTrainer(hasTrainer == 1);
            rom.setHasFourScreenVram(hasFourScreenVram == 1);
            rom.setPrgRamSize(prgRamSizeInBytes);
            rom.setPalOrNtsc(palOrNtsc);
            rom.setPrgRomBanks(prgRomBanks);
            rom.setChrRomBanks(chrRomBanks);
            return rom;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[][] readBank(InputStream stream, int bankCount, int bankSize) throws IOException {
        byte[][] banksData = new byte[bankCount][];

        for (int i = 0; i < bankCount; i++) {
            byte[] bankData = new byte[bankSize];
            stream.read(bankData);
            banksData[i] = bankData;
        }

        return banksData;
    }
}
