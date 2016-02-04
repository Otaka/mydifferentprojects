package com;

import com.nwn.data.FileReaderTpc;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 *
 * @author sad
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Start processing");
        FileReaderTpc readerTpc = new FileReaderTpc();
        BufferedImage image = readerTpc.loadFile(new FileInputStream("g:\\kotor_Extracted\\textures\\w_dirt.tpc"), "w_dirt.tpc");
        //BufferedImage image = ImageIO.read(new File("F:/1.png"));
        ImageShower.show(image);
//FileReaderGff readerGff = new FileReaderGff();
        //Gff gff=readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");

        //FileReaderTlk tlk = new FileReaderTlk();
        //Tlk tlkObject = tlk.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\dialog.tlk")), "dialog.tlk");
        //FileReaderKey keyReader = new FileReaderKey();
        //Key keyFile = keyReader.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\chitin.key")), "chitin.key");
        //FileReaderBiff bifReader = new FileReaderBiff();
        //File file = new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\data\\models.bif");
        //Biff biff = bifReader.loadFile(new FileInputStream(file), file, keyFile);
        //biff = null;
    }
}
