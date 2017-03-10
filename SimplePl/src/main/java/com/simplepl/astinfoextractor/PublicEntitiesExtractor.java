package com.simplepl.astinfoextractor;

import com.simplepl.entity.Type;
import com.simplepl.entity.Function;
import com.simplepl.entity.FileInfo;
import com.simplepl.entity.Annotation;
import com.simplepl.grammar.ast.Ast;

/**
 * @author sad
 */
public class PublicEntitiesExtractor {

    public FileInfo processAst(Ast ast) {
        if (!ast.getName().equals("module")) {
            throw new IllegalArgumentException("Expected 'module' ast, but received");
        }

        FileInfo fileInfo = new FileInfo();
        for (Ast statement : ast.getChildren()) {
            processStatement(fileInfo, statement);
        }

        return fileInfo;
    }

    private void processStatement(FileInfo fileInfo, Ast statement) {
        if (statement.getName().equals("function")) {
            parseFunction(fileInfo, statement);
        } else if (statement.getName().equals("structure")) {
            parseStructure(fileInfo, statement);
        } else if (statement.getName().equals("var")) {
            parseVariable(fileInfo, statement);
        } else if (statement.getName().equals("defineType")) {
            parseDefineType(fileInfo, statement);
        } else {
            throw new IllegalArgumentException("Not implemented parsing '" + statement.getName() + "'");
        }
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

    }
    
    private void parseVariable(FileInfo fileInfo, Ast variableAst) {

    }

    private void parseStructure(FileInfo fileInfo, Ast structureAst) {

    }

    private String extractIdentifier(Ast ast) {
        if (!ast.getName().equals("identifier")) {
            throw new IllegalArgumentException("Expected 'identifier' but found '" + ast.getName() + "'");
        }
        return (String) ast.getAttributes().get("name");
    }

    private Type parseType(Ast ast) {
        Type type = new Type();
        if (ast.getName().equals("pointer")) {
            type.setName(extractIdentifier(ast.getAttributeAst("type")));
            type.setPointer(true);
        } else if (ast.getName().equals("identifier")) {
            type.setName((String) ast.getAttributes().get("name"));
        } else {
            throw new IllegalArgumentException("Cannot parse type, because ast name is '" + ast.getName() + "'");
        }
        return type;
    }
}
