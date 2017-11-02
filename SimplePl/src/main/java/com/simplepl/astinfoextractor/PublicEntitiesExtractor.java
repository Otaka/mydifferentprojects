package com.simplepl.astinfoextractor;

import com.simplepl.entity.Argument;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.entity.Import;
import com.simplepl.entity.TypeReference;
import com.simplepl.grammar.ast.Ast;

/**
 * @author sad
 */
public class PublicEntitiesExtractor {

    public ModuleInfo processAst(Ast ast, String modulePath) {
        if (!ast.getName().equals("module")) {
            throw new IllegalArgumentException("Expected 'module' ast, but received");
        }

        ModuleInfo moduleInfo = new ModuleInfo(modulePath.toLowerCase());
        for (Ast statement : ast.getChildren()) {
            processStatement(moduleInfo, statement);
        }

        return moduleInfo;
    }

    private void processStatement(ModuleInfo moduleInfo, Ast statement) {
        switch (statement.getName()) {
            case "import":
                parseImport(moduleInfo, statement);
                break;
            case "function":
                parseFunction(moduleInfo, statement);
                break;
            /*case "structure":
                parseStructure(moduleInfo, statement);
                break;
            case "var":
                parseGlobalVariable(moduleInfo, statement);
                break;
            case "defineType":
                parseDefineType(moduleInfo, statement);
                break;*/
            default:
                throw new IllegalArgumentException("Not implemented parsing '" + statement.getName() + "'");
        }
    }

    private void parseFunction(ModuleInfo moduleInfo, Ast statement) {
        FunctionInfo function = new FunctionInfo();
        function.setName(extractIdentifier(statement.getAttributeAst("name")));
        for (Ast argument : statement.getAttributeAst("arguments").getChildren()) {
            String name = extractIdentifier(argument.getAttributeAst("name"));
            TypeReference type = parseType(argument.getAttributeAst("type"));
            Argument argumentObject = new Argument(type, name);
            function.getArguments().add(argumentObject);
        }
    }

    private TypeReference parseType(Ast astType) {
        return new TypeReference("");
    }

    private void parseImport(ModuleInfo moduleInfo, Ast importAst) {
        String path = convertImportAstToPath(importAst);
        boolean isStatic = false;
        if (importAst.getAttributeBoolean("static", false)) {
            isStatic = true;
        }

        Import importObject = new Import(path, isStatic);
        moduleInfo.getImports().add(importObject);
    }

    private String convertImportAstToPath(Ast importAst) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Ast ast : importAst.getChildren()) {
            if (first == false) {
                sb.append(".");
            }
            sb.append(extractIdentifier(ast));
            first = false;
        }
        return sb.toString();
    }

    private String extractIdentifier(Ast ast) {
        if (!ast.getName().equals("identifier")) {
            throw new IllegalArgumentException("Expected 'identifier' but found '" + ast.getName() + "'");
        }

        return (String) ast.getAttributes().get("name");
    }

    /*private void fillTypeInfoForIdentifierWithPackage(Ast ast, TypeInfo typeInfo) {
        int childsCount = ast.getChildren().size();
        Ast lastType = ast.getChildren().get(childsCount - 1);
        typeInfo.setName(lastType.getAttributeString("name"));
        StringBuilder packagePath = new StringBuilder();
        for (int i = 0; i < childsCount - 1; i++) {
            if (i != 0) {
                packagePath.append(".");
            }

            Ast type = ast.getChildren().get(i);
            packagePath.append(type.getAttributeString("name"));
        }

        typeInfo.setPackagePath(packagePath.toString());
    }*/
}
