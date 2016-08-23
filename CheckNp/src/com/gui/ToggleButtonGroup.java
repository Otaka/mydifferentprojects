package com.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JToggleButton;

/**
 * @author Dmitry
 */
public class ToggleButtonGroup {
    List<JToggleButton> buttons = new ArrayList<>();

    public ToggleButtonGroup add(final JToggleButton button) {
        buttons.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JToggleButton b : buttons) {
                    if (b != button && b.isSelected()) {
                        b.setSelected(false);
                    }
                }
                button.setSelected(true);
            }
        });
        return this;
    }
}
