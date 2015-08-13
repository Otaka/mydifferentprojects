package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.nwn.dialog.FileReaderTlk;
import com.nwn.dialog.Tlk;

/**
 *
 * @author sad
 */
public class Main {

    public static void main(String[] args) throws IOException {
        //FileReaderGff readerGff = new FileReaderGff();
        //Gff gff=readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");

        FileReaderTlk tlk = new FileReaderTlk();
        Tlk tlkObject = tlk.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\dialog.tlk")), "dialog.tlk");
    }
}
