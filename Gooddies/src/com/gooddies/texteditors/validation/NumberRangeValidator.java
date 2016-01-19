package com.gooddies.texteditors.validation;

/**
 * @author Dmitry Savchenko
 * This validator can be attached only to NumTextField
 */
public class NumberRangeValidator extends AbstractNumericValidator {

    private double minimum;
    private double maximum;
    private boolean hasMinimum = false;
    private boolean hasMaximum = false;

    public NumberRangeValidator(double minimum, double maximum, String message) {
        setMinimum(minimum);
        setMaximum(maximum);
        setErrorMessage(message);
    }

    public NumberRangeValidator(String errorMessage) {
        setErrorMessage(errorMessage);
    }

    public NumberRangeValidator setMaximum(double maximum) {
        this.maximum = maximum;
        hasMaximum = true;
        return this;
    }

    public NumberRangeValidator setMinimum(double minimum) {
        this.minimum = minimum;
        hasMinimum = true;
        return this;
    }

    @Override
    public boolean validate(double number) {
        if(hasMinimum){
            if(number<minimum){
                return false;
            }
        }
        
        if(hasMaximum){
            if(number>maximum){
                return false;
            }
        }
        return true;
    }
}
