package com;

import com.nwn.FileReaderTlk;
import java.io.*;

/**
 *
 * @author sad
 */
public class Main {

    public static void main(String[] args) throws IOException {
        //FileReaderGff readerGff = new FileReaderGff();
        //readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");

        // FileReaderBiff readerBiff = new FileReaderBiff();
        // readerBiff.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\data\\2da.bif")), "2da.bif");
        FileReaderTlk readerTlk = new FileReaderTlk();
        File file = new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\dialog.tlk");
        readerTlk.loadFile(new FileInputStream(file), file.getName(), (int) file.getTotalSpace());
    }

}
