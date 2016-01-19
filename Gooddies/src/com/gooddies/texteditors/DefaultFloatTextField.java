package com.gooddies.texteditors;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Dmitry Savchenko
 */
public class DefaultFloatTextField extends NumTextField {

    Double lastValue = null;
    // ValueChangedEvent<Float>valueChanged;

    /**
     * if min less than 0 -> automatically sets allow negative
     *
     * @param min
     * @param max
     */
    public DefaultFloatTextField(double min, double max) {
        super(TextFieldType.FLOAT);
        setFloatLength(2);
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

    /**
     * min=0, max - 9999999999999999999999999.0
     */
    public DefaultFloatTextField() {
        this(0, MAXDOUBLE);
    }

    /**
     * min=0, maximum should be >=0
     */
    public DefaultFloatTextField(double maximum) {
        this(0, maximum);
    }

    public double getValue() {
        return getvalue();
    }

    private boolean verify(String text) {
        try {
            Double.parseDouble(text);
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
                    /*  if(valueChanged!=null){
                     valueChanged.valueChanged((float)getvalue());
                     }*/
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (verify(getText())) {
                    /*   if(valueChanged!=null){
                     valueChanged.valueChanged((float)getvalue());
                     }*/
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
}
