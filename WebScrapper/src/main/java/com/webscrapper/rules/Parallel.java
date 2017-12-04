package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class Parallel extends AbstractRule {

    private final Executor executor;

    public Parallel(int parallelism, AbstractRule... actions) {
        executor = Executors.newFixedThreadPool(parallelism);
        addActions(actions);
    }

    @Override
    public void process(Context context) {
        List<Element> elements = context.getElementsOnStackTop();
        for (Element element : elements) {
            executor.execute(() -> {
                processChildElement(createElementsList(element), context);
            });
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
