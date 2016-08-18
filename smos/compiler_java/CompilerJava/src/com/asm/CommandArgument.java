package com.asm;

import com.asm.args.argresult.AbstractParsingResult;
import com.asm.args.argresult.OkResult;
import com.asm.args.matchers.AbstractMatcher;
import com.asm.exceptions.ParsingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public abstract class CommandArgument {

    private List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>();

    public void addMatcher(AbstractMatcher matcher) {
        matchers.add(matcher);
    }

    public AbstractParsingResult match(String arg) {
        return internalMatch(arg);
    }

    protected AbstractParsingResult internalMatch(String value) {
        AbstractParsingResult result = null;
        for (AbstractMatcher matcher : matchers) {
            AbstractParsingResult r = matcher.match(value);
            if (r == null) {
                return null;
            }
            result = mergeResults(result, r);
        }
        
        return result;
    }

    private AbstractParsingResult mergeResults(AbstractParsingResult res1, AbstractParsingResult res2) {
        if (res1 == null) {
            return res2;
        }

        if (res1 instanceof OkResult) {
            return res2;
        }

        if (res2 instanceof OkResult) {
            return res1;
        }

        throw new ParsingException("Cannot merge results for [" + res1.getClass().getName() + "] and [" + res2.getClass().getName() + "]");
    }
    
    public boolean isMem(){
        return false;
    }
    
    public boolean isReg(){
        return false;
    }
    
    public boolean isRM(){
        return isMem()||isReg();
    }
    
    public boolean isImm(){
        return false;
    }
}
