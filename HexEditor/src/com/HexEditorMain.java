package com;

import com.gui.MainWindow;
import com.swingson.SwingsonGuiBuilder;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * @author Dmitry
 */
public class HexEditorMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingsonGuiBuilder.setWindowsLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    final MainWindow window = new MainWindow();
                    window.pack();
                    window.setVisible(true);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                window.loadFileInternal(new File("g:\\kotor_Extracted\\mdlbinary\\dor_lda01.mdl"));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
