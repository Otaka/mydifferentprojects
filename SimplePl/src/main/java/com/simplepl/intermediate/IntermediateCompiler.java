package com.simplepl.intermediate;

import com.simplepl.astinfoextractor.ModuleTypeFinder;
import com.simplepl.astinfoextractor.PublicEntitiesExtractor;
import com.simplepl.entity.Context;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.entity.TypeReference;
import com.simplepl.entity.VariableInfo;
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
    private PublicEntitiesExtractor extractor = new PublicEntitiesExtractor();

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
        context.getScopeManager().createNewScope();
        List<Ast> expressionList = functionInfo.getExpressions().getExpressions();
        for (Ast ast : expressionList) {
            processAst(ast, functionInfo);
        }

        context.getScopeManager().removeScope();
    }

    private void processAst(Ast ast, FunctionInfo functionInfo) {
        String name = ast.getName();
        switch (name) {
            case "var":
                processVar(ast, functionInfo);
                break;
            case "binary_operation":
                processBinaryOperation(ast, functionInfo);
                break;
            default:
                throw new IllegalStateException("Unknown ast ["+name+"]");
        }
    }

    private void processVar(Ast ast, FunctionInfo functionInfo) {
        TypeReference type = extractor.parseType(ast.getAttributeAst("type"));
        String name = extractor.extractIdentifier(ast.getAttributeAst("name"));
        VariableInfo variableInfo = new VariableInfo(name, type);
        context.getScopeManager().addVariableToScope(variableInfo);
        Ast initExpression = ast.getAttributeAst("init_expression");
        if (initExpression != null) {
            //let's create ast object as if we have just variableName=value
            Ast binaryExpression=new Ast("binary_operation");
            binaryExpression.addAttribute("operation", "=");
            
            Ast variableNameAst=new Ast("identifier");
            variableNameAst.addAttribute("name", name);
            
            binaryExpression.addChild(variableNameAst);
            binaryExpression.addChild(initExpression);

            processAst(binaryExpression, functionInfo);
        }
    }
    
    private void processBinaryOperation(Ast ast, FunctionInfo functionInfo) {
        String operation=ast.getAttributeString("operation");
        
    }
}
