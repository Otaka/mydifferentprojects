package com;

import com.macro.tokenizer.Token;
import com.macro.tokenizer.Tokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author sad
 */
public class MacroProcessorMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        for (File f : new File("d:\\java\\comscore\\XPLORE_FRM_9.0_RC2_VZWCPMC_INT\\Xplore\\Professional Services\\Storm\\tools\\metadata\\scripts\\").listFiles()) {
            if (f.isFile()) {
                fileCheck(f);
            }
        }
    }

    private static void fileCheck(File f) throws Exception {
        String fileContent = IOUtils.toString(new FileInputStream(f));
        Tokenizer tokenizer = new Tokenizer(fileContent);
        StringBuilder sb = new StringBuilder();
        while (true) {
            Token t = tokenizer.nextToken();
            if (t == null) {
                break;
            }

            sb.append(t.getValue());
        }

        if (fileContent.equals(sb.toString())) {
            System.out.println("Strings equal");
        } else {
            System.out.println("Strings different");
        }
    }
}
