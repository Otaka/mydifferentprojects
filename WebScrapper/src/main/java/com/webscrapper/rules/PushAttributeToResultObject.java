package com.webscrapper.rules;

import com.webscrapper.Context;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class PushAttributeToResultObject extends AbstractRule {

    private final String attributeName;
    private final String field;
    private final boolean absUrl;

    public PushAttributeToResultObject(String attributeName, String field, boolean absUrl) {
        this.attributeName = attributeName;
        this.field = field;
        this.absUrl = absUrl;
    }

    public PushAttributeToResultObject(String attributeName, String field) {
        this.attributeName = attributeName;
        this.field = field;
        this.absUrl = false;
    }

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {
            Object currentObject = context.getCurrentObject();
            try {
                String attributeValue;
                if (absUrl == true) {
                    attributeValue = element.absUrl(attributeName);
                } else {
                    attributeValue = element.attr(attributeName);
                }
                if (currentObject instanceof Map) {
                    ((Map) currentObject).put(field, attributeValue);
                } else {
                    BeanUtils.setProperty(currentObject, field, attributeValue);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
