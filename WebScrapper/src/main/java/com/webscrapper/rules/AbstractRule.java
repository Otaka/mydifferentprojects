package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public abstract class AbstractRule {

    private final List<AbstractRule> actions = new ArrayList<>();

    protected final void addActions(AbstractRule...actions){
        for(AbstractRule action:actions){
            this.actions.add(action);
        }
    }

    public List<AbstractRule> getActions() {
        return actions;
    }


    public AbstractRule addAction(AbstractRule action) {
        actions.add(action);
        return this;
    }
    
    protected List<Element>createElementsList(Element element){
        List<Element>elementsList=new ArrayList<>();
        elementsList.add(element);
        return elementsList;
    }
    
    public abstract void process(Context context);
}
