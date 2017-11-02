package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementsByAttributeValue extends AbstractGetElement {

    public GetElementsByAttributeValue(String key, String value, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return element.getElementsByAttributeValue(key, value);
        });
    }
}
