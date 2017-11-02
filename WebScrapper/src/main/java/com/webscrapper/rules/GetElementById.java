package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementById extends AbstractGetElement {

    public GetElementById(String id, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return createElementsList(element.getElementById(id));
        });
    }
}
