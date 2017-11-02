package com.gooddies.texteditors;

/**
 * @author Dmitry Savchenko
 */
public class DefaultPercentTextField extends DefaultFloatTextField {

    public DefaultPercentTextField(double min, double max) {
        super(min, max);
        initPercentTextField();
    }

    public DefaultPercentTextField() {
        super(0, 100);
        initPercentTextField();
    }

    private void initPercentTextField() {
        setPrefixSymbol("%");
        setShowPrefixSymbol(true);
        setFloatLength(2);
        setValue(0);
    }
}
