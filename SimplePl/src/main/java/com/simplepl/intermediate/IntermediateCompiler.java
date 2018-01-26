package com.simplepl.intermediate;

import com.simplepl.astinfoextractor.ModuleTypeFinder;
import com.simplepl.entity.Context;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.intermediate.generator.BytecodeGenerator;
import java.util.List;

/**
 * @author sad
 */
public class IntermediateCompiler {

    private Context context;

    public IntermediateCompiler(Context context) {
        this.context = context;
    }

    public BytecodeModule compile(ModuleTypeFinder moduleTypeFinder, ModuleInfo moduleInfo) {
        BytecodeModule bytecodeModule = new BytecodeModule(moduleInfo);
        for (FunctionInfo function : moduleInfo.getFunctionList()) {
            processFunction(moduleTypeFinder, moduleInfo, function);
        }

        return bytecodeModule;
    }

    private BytecodeGenerator processFunction(ModuleTypeFinder moduleTypeFinder, ModuleInfo moduleInfo, FunctionInfo functionInfo) {
        BytecodeGenerator generator = new BytecodeGenerator();
        List<Ast> expressionList = functionInfo.getExpressions().getExpressions();
        for (Ast ast : expressionList) {
            processAst(ast, moduleTypeFinder, moduleInfo, functionInfo, generator);
        }
        return generator;
    }

    private void processAst(Ast ast, ModuleTypeFinder moduleTypeFinder, ModuleInfo moduleInfo, FunctionInfo functionInfo,BytecodeGenerator generator) {
        String name=ast.getName();
        if(name.equals("var")){
            
        }
    }
}
