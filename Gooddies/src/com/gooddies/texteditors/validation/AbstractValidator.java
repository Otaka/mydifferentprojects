package com.gooddies.texteditors.validation;

/**
 * @author Dmitry Savchenko
 */
public abstract class AbstractValidator {
    protected String text;

    public void setErrorMessage(String text){
        this.text=text;
    }

    public String getText(){
        return text;
    }

    public abstract boolean validate(String text);
}
