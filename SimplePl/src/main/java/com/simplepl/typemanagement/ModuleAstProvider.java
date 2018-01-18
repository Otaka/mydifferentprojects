package com.simplepl.typemanagement;

import com.simplepl.grammar.ast.Ast;

/**
 * @author sad
 */
public abstract class ModuleAstProvider {
    public abstract Ast parseModule(String module);
}
