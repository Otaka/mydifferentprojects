package com.webscrapper.rules;

/**
 * @author Dmitry
 */
public class GetElementsByTag extends AbstractGetElement {

    public GetElementsByTag(String tag, AbstractRule...actions) {
        addActions(actions);
        setFunction((context, element) -> {
            return element.getElementsByTag(tag);
        });
    }
}
