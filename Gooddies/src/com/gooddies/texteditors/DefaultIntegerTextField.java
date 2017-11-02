package com.gooddies.texteditors;

import com.gooddies.events.ValueChangedEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * @author Dmitry Savchenko
 */
public class DefaultIntegerTextField extends NumTextField {

    private Integer lastValue = null;
    private ValueChangedEvent<Integer> valueChanged;

    public DefaultIntegerTextField(int min, int max) {
        super(TextFieldType.INTEGER);
        setMinMax(min, max);
        if (min < 0) {
            setAllowNegative(true);
            if (max >= 0) {
                setValue(0);
            } else {
                setValue(max);
            }
        } else {
            setAllowNegative(false);
            setValue(min);
        }
        initListeneres();
    }

    public void setValueChanged(ValueChangedEvent<Integer> valueChanged) {
        this.valueChanged = valueChanged;
    }

    /**
     * minimum = 0; maximum=-2147483647
     */
    public DefaultIntegerTextField() {
        this(0, (int) MAXPOSITIVEINTEGER);
    }

    /**
     * minimum = 0; maximum>=0
     */
    public DefaultIntegerTextField(int maximum) {
        this(0, maximum);

    }

    public void setValue(int value) {
        super.setValue(value);
    }

    public int getValue() {
        return (int) super.getvalue();
    }

    private boolean verify(String text) {
        try {
            Integer.parseInt(text);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private void initListeneres() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (verify(getText())) {
                    if (valueChanged != null) {
                        int value = (int) getvalue();
                        valueChanged.fire(value, DefaultIntegerTextField.this);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (verify(getText())) {
                    if (valueChanged != null) {
                        int value = (int) getvalue();
                        valueChanged.fire(value, DefaultIntegerTextField.this);
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
}
