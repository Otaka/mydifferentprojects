package com.kotorresearch.script.interpreter;

import com.kotorresearch.script.data.ScriptFunction;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry
 */
public class FunctionsManager {

    private Map<Integer, ScriptFunction> functionsMap = new HashMap<>();

    public void addFunctionsHolderObject(Object functionsHolder) {
        for (Method method : functionsHolder.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(ScriptFunctionAnnotation.class) != null) {
                ScriptFunctionAnnotation annotation = method.getAnnotation(ScriptFunctionAnnotation.class);
                int index = annotation.index();
                ScriptFunction sf = new ScriptFunction(index, functionsHolder, method, method.getParameterTypes());
                functionsMap.put(index, sf);
            }
        }
    }
    
    public ScriptFunction getFunctionByIndex(int index){
        return functionsMap.get(index);
    }
}
