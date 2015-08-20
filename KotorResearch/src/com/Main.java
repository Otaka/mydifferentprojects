package com;

import com.nwn.dialog.FileReaderTlk;
import com.nwn.dialog.Tlk;
import java.io.*;

/**
 *
 * @author sad
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Start processing");
        //FileReaderGff readerGff = new FileReaderGff();
        //Gff gff=readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");

        FileReaderTlk tlk = new FileReaderTlk();
        Tlk tlkObject = tlk.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\dialog.tlk")), "dialog.tlk");
        //FileReaderKey keyReader = new FileReaderKey();
        //Key keyFile = keyReader.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\chitin.key")), "chitin.key");

        //FileReaderBiff bifReader = new FileReaderBiff();
        //File file = new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\data\\models.bif");
        //Biff biff = bifReader.loadFile(new FileInputStream(file), file, keyFile);
        //biff = null;
    }
}
