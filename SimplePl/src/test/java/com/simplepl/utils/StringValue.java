package com.simplepl.utils;

/**
 * @author sad
 */
public class StringValue implements AstValue{
    private String strValue;

    public StringValue() {
    }

    public StringValue(String value) {
        this.strValue = value;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

   
    
    
}
