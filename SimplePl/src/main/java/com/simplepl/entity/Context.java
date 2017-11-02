package com.simplepl.entity;

import com.simplepl.astinfoextractor.AstManager;
import com.simplepl.vfs.SrcRoot;

/**
 * @author sad
 */
public class Context {

    private SrcRoot srcRoot;
    private AstManager astManager;

    public Context() {
        init();
    }

    private void init() {
        srcRoot = new SrcRoot(this);
        astManager = new AstManager(this);
    }

    public AstManager getAstManager() {
        return astManager;
    }

    public SrcRoot getSrcRoot() {
        return srcRoot;
    }

}
