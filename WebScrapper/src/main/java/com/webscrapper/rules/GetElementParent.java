package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementParent extends AbstractGetElement {

    public GetElementParent(String regexp, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return createElementsList(element.parent());
        });
    }
}
