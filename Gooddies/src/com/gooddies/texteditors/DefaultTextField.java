package com.gooddies.texteditors;

import java.awt.Color;

/**
 * @author Dmitry Savchenko
 */
public class DefaultTextField extends ExtTextField {

    public DefaultTextField() {
        setBorderColor(new Color(198, 198, 198));
        setForeground(Color.BLACK);
    }

    public DefaultTextField(int max) {
        this();
        setMaxTextLength(max);
        setBorderColor(new Color(198, 198, 198));
        setForeground(Color.BLACK);
    }
}
