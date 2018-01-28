package com.simplepl.intermediate;

import com.simplepl.entity.Context;
import com.simplepl.entity.VariableInfo;
import com.simplepl.exception.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ScopeManager {

    private final List<Scope> scopes = new ArrayList<>();
    private Context context;

    public ScopeManager(Context context) {
        this.context = context;
    }
    
    public VariableInfo searchVariable(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Scope scope = scopes.get(i);
            VariableInfo varInfo = scope.getVariablesMap().get(name);
            if (varInfo != null) {
                return varInfo;
            }
        }

        return null;
    }
    
    private Scope getCurrentScope(){
        if(scopes.isEmpty()){
            throw new IllegalStateException("Cannot get last scope because there are no scopes attached");
        }

        return scopes.get(scopes.size()-1);
    }
    
    public void addVariableToScope(VariableInfo varInfo){
        if(getCurrentScope().contains(varInfo.getName())){
            throw new ParseException(0, "Variable ["+varInfo.getName()+"] already in scope");
        }

        getCurrentScope().addVariable(varInfo);
    }
    
    public void createNewScope(){
        scopes.add(new Scope());
    }

    public void removeScope(){
        if(scopes.isEmpty()){
            throw new IllegalStateException("Cannot remove scope, because scopes list is empty");
        }

        scopes.remove(scopes.size()-1);
    }
    
    
}
