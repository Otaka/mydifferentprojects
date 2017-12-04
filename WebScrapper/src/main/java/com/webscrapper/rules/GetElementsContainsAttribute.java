package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementsContainsAttribute extends AbstractGetElement {

    public GetElementsContainsAttribute(String attribute, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return element.getElementsByAttribute(attribute);
        });
    }
}
