package nesemulator;

import com.nes.ppu.Ppu;
import com.nes.processor.ALU;
import com.nes.processor.AluUtils;
import com.nes.processor.NesMemory;
import com.nes.rom.Rom;
import com.nes.rom.RomReader;
import java.io.File;
import java.io.IOException;

/**
 * @author sad
 */
public class NesEmulator {

    public static void main(String[] args) throws IOException {
        System.out.println(AluUtils.lhTo16Bit((byte) -128, (byte) -16));
        RomReader romreader = new RomReader();
        Rom rom = romreader.createRom(new File("d:\\temp\\nes\\SMBASM\\smb.nes"));
        runFile(rom);
    }

    protected static void runFile(Rom rom) throws IOException {
        ALU alu = new ALU();
        NesMemory nesMemory = new NesMemory(new Ppu(), rom);
        alu.setMemory(nesMemory);
        byte one = alu.getMemory().getValue(0xfffc);
        byte two = alu.getMemory().getValue(0xfffd);
        int pc = AluUtils.lhTo16Bit(one, two);
        alu.setPc(pc);

        // alu.getMemory().setValue(0xff, 0x2002);
        for (int i = 0;; i++) {
            String decoded = alu.decodeCommand(true);
            System.out.println(decoded);
            alu.executeCommand();
        }
    }
}
