package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public abstract class AbstractGetElement extends AbstractRule {

    private GetElementFunction function;

    public AbstractGetElement(GetElementFunction function) {
        this.function = function;
    }

    public AbstractGetElement() {
        this.function = null;
    }

    public final void setFunction(GetElementFunction function) {
        this.function = function;
    }

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {
            List<Element> childElements = function.process(context, element);
            if (childElements != null && !childElements.isEmpty()) {
                processChildElement(childElements, context);
            }
        }
    }

    protected void processChildElement(List<Element> elements, Context context) {
        try {
            context.pushElements(elements);
            for (AbstractRule rule : getActions()) {
                rule.process(context);
            }
        } finally {
            context.popElements();
        }
    }

}
