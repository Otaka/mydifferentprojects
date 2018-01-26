package com.simplepl.astinfoextractor;

import com.simplepl.entity.Argument;
import com.simplepl.entity.DefTypeInfo;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.GlobalVariableInfo;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.entity.Import;
import com.simplepl.entity.StructureField;
import com.simplepl.entity.StructureInfo;
import com.simplepl.entity.TypeReference;
import com.simplepl.exception.ParseException;
import com.simplepl.grammar.ast.Ast;

/**
 * @author sad
 */
public class PublicEntitiesExtractor {

    public ModuleInfo processAst(Ast ast, String modulePath) {
        if (!ast.getName().equals("module")) {
            throw new IllegalArgumentException("Expected 'module' ast, but received");
        }

        ModuleInfo moduleInfo = new ModuleInfo(modulePath);
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
            case "structure":
                parseStructure(moduleInfo, statement);
                break;
            case "var":
                parseGlobalVariable(moduleInfo, statement);
                break;
            case "defineType":
                parseDefineType(moduleInfo, statement);
                break;
            default:
                throw new IllegalArgumentException("Not implemented parsing '" + statement.getName() + "'");
        }
    }

    private void parseGlobalVariable(ModuleInfo moduleInfo, Ast statement) {
        GlobalVariableInfo globalVariableInfo = new GlobalVariableInfo(
                extractIdentifier(statement.getAttributeAst("name")),
                parseType(statement.getAttributeAst("type")));
        moduleInfo.getGlobalVariablesList().add(globalVariableInfo);
    }

    private void parseDefineType(ModuleInfo moduleInfo, Ast statement) {
        String newTypeName = extractIdentifier(statement.getAttributeAst("newType"));
        TypeReference typeReference = parseType(statement.getAttributeAst("source"));
        DefTypeInfo defTypeInfo = new DefTypeInfo(newTypeName, typeReference);
        moduleInfo.getDeftypesList().add(defTypeInfo);
    }

    private void parseStructure(ModuleInfo moduleInfo, Ast statement) {
        StructureInfo structure = new StructureInfo(extractIdentifier(statement.getAttributeAst("name")));
        if (statement.getChildren().isEmpty()) {
            throw new ParseException(statement, "Structure [" + structure.getName() + "] should have at least one field");
        }

        for (Ast field : statement.getChildren()) {
            String fieldName = extractIdentifier(field.getAttributeAst("name"));
            TypeReference fieldType = parseType(field.getAttributeAst("type"));
            StructureField structureField = new StructureField(fieldName, fieldType);
            structure.getFields().add(structureField);
        }

        moduleInfo.getStructuresList().add(structure);
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
        function.getExpressions().getExpressions().addAll(statement.getChildren().get(0).getChildren());
        function.setReturnType(parseType(statement.getAttributeAst("returnValue")));
        moduleInfo.getFunctionList().add(function);
    }

    private TypeReference parseType(Ast astType) {
        if (astType.getName().equals("identifier")) {
            TypeReference typeReference = new TypeReference(astType.getAttributeString("name"));
            return typeReference;
        } else if (astType.getName().equals("pointer")) {
            TypeReference typeReference = parseType(astType.getAttributeAst("type"));
            typeReference.setPointer(true);
            return typeReference;
        } else {
            throw new IllegalStateException("Expected ast type [identifier] or [pointer], but found [" + astType.getName() + "]");
        }
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
