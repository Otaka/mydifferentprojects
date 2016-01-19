package com.gooddies.swing;

import com.gooddies.persistence.Properties;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JDialog;

/**
 * @author Dmitry
 */
public abstract class JpDialog extends JDialog {

    public abstract String getDialogName();

    public JpDialog(Window parent) throws HeadlessException {
        super(parent);
        init();
    }

    private void init() {
        int width = Properties.get().getInt(getDialogName() + ".width", 800);
        int height = Properties.get().getInt(getDialogName() + ".height", 600);

        setSize(width, height);

        setLocation(Properties.get().getInt(getDialogName() + ".x", 20),
                Properties.get().getInt(getDialogName() + ".y", 20));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                Properties.get().putInt(getDialogName() + ".x", getLocation().x);
                Properties.get().putInt(getDialogName() + ".y", getLocation().y);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                Properties.get().putInt(getDialogName() + ".width", getSize().width);
                Properties.get().putInt(getDialogName() + ".height", getSize().height);
            }
        });
    }
}
