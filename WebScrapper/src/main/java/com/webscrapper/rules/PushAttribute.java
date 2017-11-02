package com.webscrapper.rules;

import com.webscrapper.Context;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class PushAttribute extends AbstractRule {

    private final String attributeName;

    public PushAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {
            context.addArbitraryResult(element.attr(attributeName));
        }
    }
}
