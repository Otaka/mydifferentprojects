package com.webscrapper.rules;

import com.webscrapper.Context;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class PushInnerText extends AbstractRule {

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {
            context.addArbitraryResult(element.text());
        }
    }

}
