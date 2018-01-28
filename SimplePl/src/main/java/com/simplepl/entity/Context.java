package com.simplepl.entity;

import com.simplepl.astinfoextractor.AstManager;
import com.simplepl.astinfoextractor.TypeManager;
import com.simplepl.intermediate.ScopeManager;
import com.simplepl.vfs.SrcRoot;

/**
 * @author sad
 */
public class Context {

    private SrcRoot srcRoot;
    private AstManager astManager;
    private TypeManager typeManager;
    private ScopeManager scopeManager;

    public Context() {
        init();
    }

    private void init() {
        srcRoot = new SrcRoot(this);
        astManager = new AstManager(this);
        typeManager = new TypeManager();
        scopeManager = new ScopeManager(this);
    }

    public ScopeManager getScopeManager() {
        return scopeManager;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }

    public AstManager getAstManager() {
        return astManager;
    }

    public SrcRoot getSrcRoot() {
        return srcRoot;
    }

}
