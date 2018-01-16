package com;

import com.nwn.data.*;
import com.nwn.data.biff.BiffEntry;
import com.nwn.data.erf.Erf;
import com.nwn.data.key.ResourceType;
import com.nwn.data.tpc.TpcTexture;
import com.nwn.utils.TpcConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sad
 */
public class Main {

    private static void tryToExtractTextures(String archiveName) throws IOException {
        FileReaderErf erfReader = new FileReaderErf();
        File file = new File("h:\\Games\\Games\\SteamLibrary\\steamapps\\common\\swkotor\\TexturePacks\\" + archiveName);
        Erf erf = erfReader.loadFile(new FileInputStream(file), file);
        FileReaderTpc readerTpc = new FileReaderTpc();
        int i = -1;
        List<String> errorTextures = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            for (BiffEntry biffEntry : erf.getResourceEntries()) {
                i++;
                String name = biffEntry.getResourceId().getResRef();
                if (biffEntry.getResourceId().getResourceType() == ResourceType.TPC) {
                    System.out.format("%04d Texture %s.tpc\n", i, name);
                    File output = null;
                    try {
                        inputStream.getChannel().position(biffEntry.getOffset());
                        TpcTexture texture = readerTpc.loadFile(inputStream, biffEntry.getSize(), name);
                        output = new File("g:\\kotor_Extracted\\convertedTextures\\" + name + ".png");
                        new TpcConverter().saveTpcAsPng(texture, output);
                    } catch (Exception ex) {
                        System.out.println("Exception while process texture " + name + ".tpc");
                        ex.printStackTrace(System.out);
                        errorTextures.add(name);
                        if (output != null) {
                            output.delete();
                        }
                    }
                } else {
                    System.out.println("Texture " + name + "." + biffEntry.getResourceId().getResourceType().name() + "   SKIP");
                }
            }
        }

        if (!errorTextures.isEmpty()) {
            System.out.println("Errors(" + errorTextures.size() + "):");
            for (String errorName : errorTextures) {
                System.out.println(errorName);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Start processing");
        //tryToExtractTextures("swpc_tex_gui.erf");
        tryToExtractTextures("swpc_tex_tpa.erf");
        /*FileReaderErf erfReader = new FileReaderErf();
         File file = new File("h:\\Games\\Games\\StarWarsKnightsOfTheOldRepublic\\TexturePacks\\swpc_tex_gui.erf");
         Erf erf = erfReader.loadFile(new FileInputStream(file), file);
         BiffEntry entry = erf.getResourceEntries()[1000];
         FileInputStream inputStream = new FileInputStream(file);
         inputStream.skip(entry.getOffset());
         try (FileOutputStream fout = new FileOutputStream(new File("f:/" + entry.getResourceId().getResRef() + "." + entry.getResourceId().getResourceType()))) {
         for (int i = 0; i < entry.getSize(); i++) {
         int b = inputStream.read();
         fout.write(b);
         }
         }
         inputStream.close();*/

 /* FileReaderTpc readerTpc = new FileReaderTpc();
         File file = new File("g:\\kotor_Extracted\\textures\\LMG_Water01B.tpc");
         TpcTexture texture = readerTpc.loadFile(new FileInputStream(file), (int) file.length(), file.getName());
         new TpcConverter().saveTpc(texture, new File("g:\\kotor_Extracted\\textures\\LMG_Water01B.png"));*/
        //BufferedImage image = ImageIO.read(new File("F:/1.png"));
        //ImageShower.show(image);
//FileReaderGff readerGff = new FileReaderGff();
        //Gff gff=readerGff.loadFile(new FileInputStream(new File("d:\\temp\\nwn\\test files\\end_carth001.dlg")), "end_carth001.dlg");
        //FileReaderTlk tlk = new FileReaderTlk();
        //Tlk tlkObject = tlk.loadFile(new FileInputStream(new File("h:\\Games\\Games\\Star Wars Knights of the Old Republic\\dialog.tlk")), "dialog.tlk");
        /*FileReaderKey keyReader = new FileReaderKey();
         Key keyFile = keyReader.loadFile(new FileInputStream(new File("h:\\Games\\Games\\StarWarsKnightsOfTheOldRepublic\\chitin.key")), "chitin.key");
         List<String> names = new ArrayList<>(keyFile.getKeyResources().length);
         for (KeyResource key : keyFile.getKeyResources()) {
         if (key.getResRef().toLowerCase().contains("ia_class6_002")) {
         int x = 0;
         x++;
         System.out.println("key name [" + key.getResRef() + "]");
         }
         names.add(key.toString());
         }
         Collections.sort(names);
         PrintStream stream = new PrintStream("f:/1.txt");
         for (String line : names) {
         stream.println(line);
         }
         stream.close();

         FileReaderBiff bifReader = new FileReaderBiff();
         File file = new File("h:\\Games\\Games\\StarWarsKnightsOfTheOldRepublic\\data\\models.bif");
         Biff biff = bifReader.loadFile(new FileInputStream(file), file, keyFile);
         for (BiffEntry entry : biff.getEntries()) {
         if (entry.getResourceId().getResRef().toLowerCase().contains("ia_class6_002")) {
         int x = 0;
         x++;
         System.out.println("2key name [" + entry.getResourceId().getResRef() + "]");
         }
         }*/
    }
}
