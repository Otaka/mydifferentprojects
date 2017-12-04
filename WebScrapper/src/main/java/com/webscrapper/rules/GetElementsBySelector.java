package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementsBySelector extends AbstractGetElement {

    public GetElementsBySelector(String selector, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return element.select(selector);
        });
    }
}
