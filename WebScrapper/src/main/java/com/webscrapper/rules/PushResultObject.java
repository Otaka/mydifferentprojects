package com.webscrapper.rules;

import com.webscrapper.Context;

/**
 * @author Dmitry
 */
public class PushResultObject extends AbstractRule {
    
    private final Class clazz;
    
    public PushResultObject(Class clazz) {
        this.clazz=clazz;
    }

    @Override
    public void process(Context context) {
        try {
            context.pushCurrentObject(clazz.newInstance());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
