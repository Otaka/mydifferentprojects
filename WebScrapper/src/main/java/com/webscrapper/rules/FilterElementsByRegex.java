package com.webscrapper.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class FilterElementsByRegex extends AbstractGetElement {

    public FilterElementsByRegex(String attribute, String regex, AbstractRule... actions) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        addActions(actions);
        setFunction((context, element) -> {
            String attr = element.attr(attribute);
            List<Element> elements = new ArrayList<>();
            if (pattern.matcher(attr).matches()) {
                elements.add(element);
            }

            return elements;
        });
    }

}
