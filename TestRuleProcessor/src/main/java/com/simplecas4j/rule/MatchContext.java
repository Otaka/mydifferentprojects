package com.simplecas4j.rule;

import com.simplecas4j.ast.Ast;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class MatchContext {
    private Map<String, Ast>matchedAsts=new HashMap<>();

    public Map<String, Ast> getMatchedAsts() {
        return matchedAsts;
    }

    public void addMatchedAst(String label, Ast astHolder){
        matchedAsts.put(label, astHolder);
    }
    
    public Ast getAstHolder(String label){
        return matchedAsts.get(label);
    }
    
    public Ast getAstHolderThrowIfNotFound(String label,String errorMessage){
        Ast astHolder= matchedAsts.get(label);
        if(astHolder==null){
            throw new IllegalArgumentException(errorMessage.replace("{label}", errorMessage));
        }
        return astHolder;
    }
}
