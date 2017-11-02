package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 *
 * @author Dmitry
 */
public interface GetElementFunction {
    public List<Element>process(Context context, Element element);
}
