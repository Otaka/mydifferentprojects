package com;

import com.gui.MainFrame;
import com.swingson.SwingsonGuiBuilder;
import javax.swing.SwingUtilities;

/**
 * @author Dmitry
 */
public class CheckNpMain {

    public static void main(String[] args) {
        SwingsonGuiBuilder.setWindowsLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);

            }
        });
    }
}
