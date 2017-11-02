package com.webscrapper.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry process elements with values that are not seen before<br/>
 * for example check not processed href urls
 */
public class FilterNotProcessedAttributeValues extends AbstractGetElement {
    private Set<String>seenValues=new HashSet<>();
    public FilterNotProcessedAttributeValues(String attribute, AbstractRule... actions) {
        addActions(actions);
        setFunction((context, element) -> {
            List<Element> elements = new ArrayList<>();
            String attributeValue=element.attr(attribute).toLowerCase();
            if(seenValues.contains(attributeValue)){
                return elements;
            }

            seenValues.add(attributeValue);
            elements.add(element);
            return elements;
        });
    }
}
