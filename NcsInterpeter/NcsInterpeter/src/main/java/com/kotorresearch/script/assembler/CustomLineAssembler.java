package com.kotorresearch.script.assembler;

import java.io.ByteArrayOutputStream;

/**
 * @author Dmitry
 */
public interface CustomLineAssembler {

    public void assembleLine(OpcodeHandler assembleLine, String line, ByteArrayOutputStream output, int startOfCommandOffset);

}
