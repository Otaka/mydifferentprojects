package com.gooddies.texteditors.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry Savchenko
 */
public class RegExpValidator extends AbstractValidator{
    protected String regExp;
    protected Pattern pattern=null;
    
    public RegExpValidator(String pattern, String message){
        setPattern(pattern);
        setErrorMessage(message);
    }

    public String getPattern(){
        return regExp;
    }
    
    public void setPattern(String pattern){
        regExp=pattern;
        createPattern();
    }
    
    private void createPattern(){
        try{
            pattern=Pattern.compile(regExp);
        }catch(Exception ex){
            pattern=null;
            ex.printStackTrace();
        }
    }

    @Override
    public boolean validate(String text)
    {
        if(pattern!=null){
            Matcher m=pattern.matcher(text);
            if(m.matches()){
                return true;
            }else{
                return false;
            }
        }else{
            try{
                throw new Exception("Validator is null");
            }catch(Exception ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return true;
    }

}
