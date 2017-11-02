package com.gooddies.texteditors.validation;

/**
 * @author Dmitry
 */
public class LengthValidator extends AbstractValidator{

    private int length;

    public LengthValidator(int maxLength, String message){
        length=maxLength;
        setErrorMessage(message);
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length=length;
    }

    @Override
    public boolean validate(String text)
    {
        if(text.length()>length)return false;
        return true;
    }

}
