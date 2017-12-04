package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class GetElementsByIndex extends AbstractGetElement {
private int index=0;
    public GetElementsByIndex(int index, AbstractRule...actions) {
        this.index=index;
        addActions(actions);
    }
    
     @Override
    public void process(Context context) {
        List<Element>elements= context.getElementsOnStackTop();
        if(elements.size()<=index){
            Element element=elements.get(index);
            processChildElement(createElementsList(element), context);
        }
    }
    
}
