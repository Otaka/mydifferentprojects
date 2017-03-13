package com.simplepl.astinfoextractor;

import com.simplepl.entity.Type;
import com.simplepl.entity.Function;
import com.simplepl.entity.FileInfo;
import com.simplepl.entity.Annotation;
import com.simplepl.entity.Import;
import com.simplepl.entity.Structure;
import com.simplepl.entity.Variable;
import com.simplepl.grammar.ast.Ast;

/**
 * @author sad
 */
public class PublicEntitiesExtractor {

    public FileInfo processAst(Ast ast, String packagePath) {
        if (!ast.getName().equals("module")) {
            throw new IllegalArgumentException("Expected 'module' ast, but received");
        }

        FileInfo fileInfo = new FileInfo(packagePath);
        for (Ast statement : ast.getChildren()) {
            processStatement(fileInfo, statement);
        }

        return fileInfo;
    }

    private void processStatement(FileInfo fileInfo, Ast statement) {
        switch (statement.getName()) {
            case "function":
                parseFunction(fileInfo, statement);
                break;
            case "structure":
                parseStructure(fileInfo, statement);
                break;
            case "var":
                parseGlobalVariable(fileInfo, statement);
                break;
            case "defineType":
                parseDefineType(fileInfo, statement);
                break;
            case "import":
                parseImport(fileInfo, statement);
                break;
            default:
                throw new IllegalArgumentException("Not implemented parsing '" + statement.getName() + "'");
        }
    }

    private void parseImport(FileInfo fileInfo, Ast functionAst) {
        Import importObject = new Import();
        fileInfo.getImports().add(importObject);
    }

    private void parseFunction(FileInfo fileInfo, Ast functionAst) {
        Function function = new Function();
        if (functionAst.getAttributes().containsKey("annotations")) {
            Ast annotations = (Ast) functionAst.getAttributes().get("annotations");
            for (Ast annotation : annotations.getChildren()) {
                function.getAnnotations().add(new Annotation((String) annotation.getAttributes().get("name")));
            }
        }

        function.setName(extractIdentifier(functionAst.getAttributeAst("name")));
        function.setReturnValue(parseType(functionAst.getAttributeAst("returnValue")));
        fileInfo.getFunctionList().add(function);
    }

    private void parseDefineType(FileInfo fileInfo, Ast variableAst) {
        Type newType = new Type();
        newType.setPackagePath(fileInfo.getPackagePath());
        newType.setName(extractIdentifier(variableAst.getAttributeAst("newType")));
        newType.setParent(parseType(variableAst.getAttributeAst("source")));
        fileInfo.getTypes().add(newType);
    }

    private void parseGlobalVariable(FileInfo fileInfo, Ast variableAst) {
        Variable globalVariable = parseVariable(variableAst);
        fileInfo.getGlobalVariables().add(globalVariable);
    }

    private void parseStructure(FileInfo fileInfo, Ast structureAst) {
        Structure structure = new Structure();
        structure.setName(extractIdentifier(structureAst.getAttributeAst("name")));
        for (Ast structureField : structureAst.getChildren()) {
            structure.getFields().add(parseVariable(structureField));
        }

        fileInfo.getStructures().add(structure);
    }

    private Variable parseVariable(Ast ast) {
        if (!ast.getName().equals("var")) {
            throw new IllegalArgumentException("Expect 'var' ast, but found '" + ast.getName() + "'");
        }

        Type type = parseType(ast.getAttributeAst("type"));
        String fieldName = extractIdentifier(ast.getAttributeAst("name"));
        Variable variable = new Variable(type, fieldName);
        return variable;
    }

    private String extractIdentifier(Ast ast) {
        if (!ast.getName().equals("identifier")) {
            throw new IllegalArgumentException("Expected 'identifier' but found '" + ast.getName() + "'");
        }

        return (String) ast.getAttributes().get("name");
    }

    private Type parseType(Ast ast) {
        Type type = new Type();
        switch (ast.getName()) {
            case "pointer":
                type.setName(extractIdentifier(ast.getAttributeAst("type")));
                type.setPointer(true);
                break;
            case "identifier":
                type.setName((String) ast.getAttributes().get("name"));
                break;
            default:
                throw new IllegalArgumentException("Cannot parse type, because ast name is '" + ast.getName() + "'");
        }

        return type;
    }
}
