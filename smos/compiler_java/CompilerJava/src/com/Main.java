package com;

import com.jnr.x86asm.Assembler;
import com.jnr.x86asm.CPU;
import com.utils.Utils;
import java.nio.ByteBuffer;

/**
 *
 * @author sad
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Assembler assembler = new Assembler(CPU.X86_32);
        assembler.setOldCompiler(true);
        assembler.interrupt(10);

        ByteBuffer buffer = ByteBuffer.allocate(assembler.codeSize());
        assembler.relocCode(buffer, 0);
        byte[] bytes = buffer.array();
        System.out.println(Utils.bytesToHex(bytes));
    }

}
