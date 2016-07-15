package com.sqlparserproject.ast.helperobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class SectionObj {

    private String sectionName;
    private Map<String, Object> values = new HashMap<String, Object>();

    public SectionObj(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }
    
    public boolean contains(String key){
        return values.containsKey(key);
    }

    public void pushValue(String key, Object value) {
        values.put(key, value);
    }

    public Object getValue(String key) {
        return values.get(key);
    }
    
    public String getString(String key) {
        return (String) values.get(key);
    }

}
