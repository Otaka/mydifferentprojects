package com.gooddies.swing;

import com.gooddies.persistence.Properties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Dmitry
 */
public abstract class JpWindow extends JFrame {

    public abstract String getWindowName();

    public JpWindow() throws HeadlessException {
        //init();
    }

    public void setSavedPosition() {
        int width = Properties.get().getInt(getWindowName() + ".width", 800);
        int height = Properties.get().getInt(getWindowName() + ".height", 600);

        setSize(width, height);
        if (Properties.get().getBoolean(getWindowName() + ".maximized", false)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setLocation(Properties.get().getInt(getWindowName() + ".x", 20),
                Properties.get().getInt(getWindowName() + ".y", 20));
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                Properties.get().putInt(getWindowName()+".x", getLocation().x);
                Properties.get().putInt(getWindowName()+".y", getLocation().y);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                Properties.get().putBoolean(getWindowName()+".maximized", (getExtendedState() & MAXIMIZED_BOTH) != 0);
                Properties.get().putInt(getWindowName()+".width", getSize().width);
                Properties.get().putInt(getWindowName()+".height", getSize().height);
            }
        });
    }
}
