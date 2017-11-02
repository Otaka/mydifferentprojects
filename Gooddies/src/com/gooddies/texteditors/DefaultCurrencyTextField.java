package com.gooddies.texteditors;


/**
 * @author Dmitry Savchenko
 */
public class DefaultCurrencyTextField extends DefaultFloatTextField {
    
    public DefaultCurrencyTextField(double min, double max) {
        super(min, max);
        setPrefixSymbol("$");
        setShowPrefixSymbol(true);
        setFloatLength(2);
        setValue(0);
    }
}
