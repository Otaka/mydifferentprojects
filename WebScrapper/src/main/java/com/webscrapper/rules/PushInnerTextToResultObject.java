package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class PushInnerTextToResultObject extends AbstractRule {

    private final String field;

    public PushInnerTextToResultObject(String field) {
        this.field = field;
    }

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {
            Object currentObject = context.getCurrentObject();
            try {
                if (currentObject instanceof Map) {
                    ((Map) currentObject).put(field, element.ownText());
                } else {
                    BeanUtils.setProperty(currentObject, field, element.ownText());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
