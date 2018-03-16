package com.simplecas4j.rule;

import com.simplecas4j.ast.AstHolder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class MatchContext {
    private Map<String, AstHolder>matchedAsts=new HashMap<>();

    public Map<String, AstHolder> getMatchedAsts() {
        return matchedAsts;
    }

    public void addMatchedAst(String label, AstHolder astHolder){
        matchedAsts.put(label, astHolder);
    }
    
    public AstHolder getAstHolder(String label){
        return matchedAsts.get(label);
    }
}
