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
    private ModuleInfo moduleInfo;
    private ModuleTypeFinder moduleTypeFinder;
    private BytecodeGenerator bytecodeGenerator;

    public IntermediateCompiler(Context context, ModuleInfo moduleInfo, ModuleTypeFinder moduleTypeFinder) {
        this.context = context;
        this.moduleInfo = moduleInfo;
        this.moduleTypeFinder = moduleTypeFinder;
        this.bytecodeGenerator = new BytecodeGenerator();
    }

    public BytecodeModule compile() {
        BytecodeModule bytecodeModule = new BytecodeModule(moduleInfo);
        for (FunctionInfo function : moduleInfo.getFunctionList()) {
            processFunction(function);
        }

        return bytecodeModule;
    }

    private void processFunction(FunctionInfo functionInfo) {
        bytecodeGenerator.clear();
        List<Ast> expressionList = functionInfo.getExpressions().getExpressions();
        for (Ast ast : expressionList) {
            processAst(ast, functionInfo);
        }
    }

    private void processAst(Ast ast, FunctionInfo functionInfo) {
        String name = ast.getName();
        if (name.equals("var")) {
            processVar(ast, functionInfo);
        }
    }

    private void processVar(Ast ast, FunctionInfo functionInfo) {

    }
}
