package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementsMatchingOwnText extends AbstractGetElement {

    public GetElementsMatchingOwnText(String regexp, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return element.getElementsMatchingOwnText(regexp);
        });
    }
}
