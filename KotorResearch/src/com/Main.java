package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import nwn.FileReaderGff;

/**
 *
 * @author sad
 */
public class Main {

    public static void main(String[] args) throws IOException {
        FileReaderGff readerGff = new FileReaderGff();
        readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");
    }

}
