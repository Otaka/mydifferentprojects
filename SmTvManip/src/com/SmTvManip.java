package com;

import com.gui.MainFrame;
import com.swingson.SwingsonGuiBuilder;
import javax.swing.SwingUtilities;

public class SmTvManip {

    public static void main(String[] args) {
        SwingsonGuiBuilder.setWindowsLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame=new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
}
