package com.gooddies.texteditors.validation;

public class NotZeroValidator extends AbstractNumericValidator{

    public NotZeroValidator(String message){
        setErrorMessage(message);
    }
    
    @Override
    public boolean validate(double number) {
        if(number==0){
            return false;
        }
        return true;
    }
}
