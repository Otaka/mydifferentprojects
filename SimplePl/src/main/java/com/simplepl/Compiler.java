package com.simplepl;

import com.simplepl.astinfoextractor.PublicEntitiesExtractor;
import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.intermediate.BytecodeModule;
import com.simplepl.intermediate.IntermediateCompiler;

/**
 * @author sad
 */
public class Compiler {

    private Context context;

    public Compiler() {
        this.context = new Context();
    }

    public Context getContext() {
        return context;
    }

    public void compileProgram(String module) {
        context.getAstManager().getModuleInfo(module);
        for (ModuleInfo mi : context.getAstManager().getListOfModules()) {
            compileModule(mi);
        }
    }

    private void compileModule(ModuleInfo moduleInfo) {
        Ast moduleAst = context.getAstManager().getModuleAst(moduleInfo.getModule());
        PublicEntitiesExtractor entitiesExtractor = new PublicEntitiesExtractor();
        ModuleInfo fullModuleInfo = entitiesExtractor.processAst(moduleAst, moduleInfo.getModule());
        context.getAstManager().fixTypeReferences(fullModuleInfo);
        fullModuleInfo.setTypesProcessed(true);
        IntermediateCompiler intermediateCompiler = new IntermediateCompiler(context,fullModuleInfo,fullModuleInfo.createModuleTypeFinder(context));
        BytecodeModule bytecodeModule = intermediateCompiler.compile();
    }
}
