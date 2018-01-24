package com.simplepl.astinfoextractor;

import com.simplepl.entity.Argument;
import com.simplepl.entity.Context;
import com.simplepl.entity.DefTypeInfo;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.GlobalVariableInfo;
import com.simplepl.entity.Import;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.entity.StructureField;
import com.simplepl.entity.StructureInfo;
import com.simplepl.entity.TypeReference;
import com.simplepl.entity.types.Type;
import com.simplepl.exception.ParseException;
import com.simplepl.grammar.MainParser;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.vfs.AbstractFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author sad
 */
public class AstManager {

    private Context context;
    private Map<String, Ast> parsedSourceFiles = new HashMap<>();
    private Map<String, ModuleInfo> moduleInfoMap = new HashMap<>();

    public AstManager(Context context) {
        this.context = context;
    }

    public List<ModuleInfo>getListOfModules(){
        return new ArrayList<>(moduleInfoMap.values());
    }
    
    public ModuleInfo getModuleInfo(String module) {
        ModuleInfo moduleInfo = getModuleInfoWithoutPreprocess(module);
        if (moduleInfo.isTypesProcessed() == false) {
            postProcessTypes(moduleInfo);
        }

        return moduleInfo;
    }

    public Ast getModuleAst(String modulePath) {
        Ast ast = parsedSourceFiles.get(modulePath.toLowerCase());
        if (ast != null) {
            return ast;
        }

        AbstractFile file = context.getSrcRoot().getAbstractFile(modulePath);
        String sourceText = file.readToString();
        Ast moduleAst = parseSource(sourceText, modulePath);
        parsedSourceFiles.put(modulePath, ast);
        return moduleAst;
    }

    private Ast parseSource(String sourceText, String module) {
        MainParser parser = createParser();
        ParseRunner runner = new BasicParseRunner(parser.main());
        ParsingResult<Object> articleResult = runner.run(sourceText);
        if (!articleResult.matched) {
            System.out.println("ERROR while parsing module " + module);
            for (ParseError error : articleResult.parseErrors) {
                System.out.println("" + error.getStartIndex() + error.getErrorMessage());
            }
            throw new ParseException(0, "Cannot compile module " + module);
        }

        Ast ast = (Ast) articleResult.valueStack.pop();
        if (!articleResult.valueStack.isEmpty()) {
            Object obj = articleResult.valueStack.pop();
            throw new IllegalStateException("Internal compiler error while parsing " + module + ". Value stack should have only one value at the end, but it contains [" + obj);
        }

        return ast;
    }

    private MainParser createParser() {
        MainParser parser = Parboiled.createParser(MainParser.class);
        return parser;
    }

    protected ModuleInfo getModuleInfoWithoutPreprocess(String module) {
        ModuleInfo moduleInfo = moduleInfoMap.get(module.toLowerCase());
        if (moduleInfo == null) {
            Ast moduleAst = getModuleAst(module);
            PublicEntitiesExtractor entitiesExtractor = new PublicEntitiesExtractor();
            moduleInfo = entitiesExtractor.processAst(moduleAst, module);
            moduleInfoMap.put(module.toLowerCase(), moduleInfo);
        }

        return moduleInfo;
    }

    public void postProcessTypes(ModuleInfo moduleInfo) {
        Set<ModuleInfo> allNotProcessedModuleInfos = new HashSet<>();
        collectAllNotProcessedModuleInfos(moduleInfo, allNotProcessedModuleInfos);
        Map<String, Type> foundTypes = collectTypes(allNotProcessedModuleInfos);
        fixTypeReferences(foundTypes, allNotProcessedModuleInfos);
        for (ModuleInfo mi : allNotProcessedModuleInfos) {
            mi.setTypesProcessed(true);
        }
    }

    private void collectAllNotProcessedModuleInfos(ModuleInfo moduleInfo, Set<ModuleInfo> collectedModuleInfos) {
        collectedModuleInfos.add(moduleInfo);
        for (Import importObject : moduleInfo.getImports()) {
            ModuleInfo importedModuleInfo = getModuleInfoWithoutPreprocess(importObject.getPath());
            if (!collectedModuleInfos.contains(importedModuleInfo)) {
                if (!importedModuleInfo.isTypesProcessed()) {
                    collectAllNotProcessedModuleInfos(importedModuleInfo, collectedModuleInfos);
                }
            }
        }
    }

    private Map<String, Type> collectTypes(Set<ModuleInfo> modules) {
        Map<String, Type> foundTypes = new HashMap<>();
        for (ModuleInfo mi : modules) {
            for (StructureInfo structure : mi.getStructuresList()) {
                Type structureType = new Type();
                structureType.setOwnerModule(mi);
                String typeName = mi.getModule() + "." + structure.getName();
                structureType.setTypeName(typeName);
                structureType.setInternal(structure);
                errorIfTypeExists(foundTypes, typeName, "Type");
                foundTypes.put(typeName, structureType);
                context.getTypeManager().addType(typeName, structureType);
            }

            for (FunctionInfo function : mi.getFunctionList()) {
                Type functionType = new Type();
                functionType.setOwnerModule(mi);
                String typeName = mi.getModule() + "." + function.getName();
                functionType.setTypeName(typeName);
                functionType.setInternal(function);
                errorIfTypeExists(foundTypes, typeName, "Function");
                foundTypes.put(typeName, functionType);
                context.getTypeManager().addType(typeName, functionType);
            }

            for (DefTypeInfo defType : mi.getDeftypesList()) {
                Type defTypeType = new Type();
                defTypeType.setParent(defType.getTypeReference());
                defTypeType.setOwnerModule(mi);
                String typeName = mi.getModule() + "." + defType.getName();
                defTypeType.setTypeName(typeName);
                defTypeType.setInternal(defType);
                errorIfTypeExists(foundTypes, typeName, "Type");
                foundTypes.put(typeName, defTypeType);
                context.getTypeManager().addType(typeName, defTypeType);
            }
        }

        return foundTypes;
    }

    private ModuleTypeFinder getModuleTypeFinder(ModuleInfo moduleInfo, Map<ModuleInfo, ModuleTypeFinder> moduleToTypeFinderMap) {
        ModuleTypeFinder moduleTypeFinder = moduleToTypeFinderMap.get(moduleInfo);
        if (moduleTypeFinder != null) {
            return moduleTypeFinder;
        }

        moduleTypeFinder = new ModuleTypeFinder(context);
        moduleTypeFinder.addImport(moduleInfo.getModule());
        for (Import importObject : moduleInfo.getImports()) {
            moduleTypeFinder.addImport(importObject.getPath());
        }

        moduleToTypeFinderMap.put(moduleInfo, moduleTypeFinder);
        return moduleTypeFinder;
    }

    private void fixTypeReferences(Map<String, Type> foundTypes, Set<ModuleInfo> modules) {
        Map<ModuleInfo, ModuleTypeFinder> moduleToTypeFinderMap = new HashMap<>();

        for (Type type : foundTypes.values()) {
            if (type.getInternal() == null) {
                throw new IllegalStateException("type.getInternal should not be null. Type [" + type.getOwnerModule().getModule() + "." + type.getTypeName() + "]");
            } else if (type.getInternal() instanceof StructureInfo) {
                StructureInfo si = (StructureInfo) type.getInternal();
                fixStructureType(type, si, getModuleTypeFinder(type.getOwnerModule(), moduleToTypeFinderMap));
            } else if (type.getInternal() instanceof FunctionInfo) {
                FunctionInfo fi = (FunctionInfo) type.getInternal();
                fixFunctionType(type, fi, getModuleTypeFinder(type.getOwnerModule(), moduleToTypeFinderMap));
            } else if (type.getInternal() instanceof DefTypeInfo) {
                DefTypeInfo defType = (DefTypeInfo) type.getInternal();
                fixDefTypeType(type, defType, getModuleTypeFinder(type.getOwnerModule(), moduleToTypeFinderMap));
            } else {
                throw new IllegalArgumentException("Unimplemented fixTypeReferences for type " + type.getInternal().getClass());
            }
        }

        //fix global variables
        for (ModuleInfo moduleInfo : modules) {
            for (GlobalVariableInfo gvi : moduleInfo.getGlobalVariablesList()) {
                fixGlobalVariableType(gvi.getType(), gvi, getModuleTypeFinder(moduleInfo, moduleToTypeFinderMap));
            }
        }
    }

    private void fixGlobalVariableType(TypeReference type, GlobalVariableInfo globalVariable, ModuleTypeFinder moduleTypeFinder) {
        String typeName = globalVariable.getType().getTypeName();
        Type realParentType = moduleTypeFinder.searchTypeForModule(typeName);
        if (realParentType == null) {
            throw new ParseException(0, "Cannot find type [" + typeName + "] typed in global variable [" + globalVariable.getName() + "]");
        }

        type.setType(realParentType);
    }

    private void fixDefTypeType(Type type, DefTypeInfo deftypeInfo, ModuleTypeFinder moduleTypeFinder) {
        String typeName = deftypeInfo.getTypeReference().getTypeName();
        Type realParentType = moduleTypeFinder.searchTypeForModule(typeName);
        if (realParentType == null) {
            throw new ParseException(0, "Cannot find type [" + typeName + "] typed in deftype [" + deftypeInfo.getName() + "]");
        }

        type.getParent().setType(realParentType);
    }

    private void fixStructureType(Type type, StructureInfo structureInfo, ModuleTypeFinder moduleTypeFinder) {
        for (StructureField sf : structureInfo.getFields()) {
            String typeName = sf.getType().getTypeName();
            Type realFieldType = moduleTypeFinder.searchTypeForModule(typeName);
            if (realFieldType == null) {
                throw new ParseException(0, "Cannot find type [" + typeName + "] for field [" + sf.getName() + "] in structure [" + type.getOwnerModule().getModule() + "." + structureInfo.getName() + "]");
            }

            sf.getType().setType(realFieldType);
        }
    }

    private void fixFunctionType(Type type, FunctionInfo functionInfo, ModuleTypeFinder moduleTypeFinder) {
        int index = -1;
        for (Argument argument : functionInfo.getArguments()) {
            index++;
            String typeName = argument.getType().getTypeName();
            Type realFieldType = moduleTypeFinder.searchTypeForModule(typeName);
            if (realFieldType == null) {
                throw new ParseException(0, "Cannot find type [" + typeName + "] for function #" + index + " argument [" + argument.getName() + "] in function [" + type.getOwnerModule().getModule() + "." + functionInfo.getName() + "()]");
            }
            argument.getType().setType(realFieldType);
        }

        String typeName = functionInfo.getReturnType().getTypeName();
        Type realFieldType = moduleTypeFinder.searchTypeForModule(typeName);
        if (realFieldType == null) {
            throw new ParseException(0, "Cannot find type [" + typeName + "] for return type of function [" + type.getOwnerModule().getModule() + "." + functionInfo.getName() + "()]");
        }

        functionInfo.getReturnType().setType(realFieldType);
    }

    private void errorIfTypeExists(Map<String, Type> foundTypes, String typeName, String typeString) {
        Type type = foundTypes.get(typeName);
        if (type != null) {
            throw new ParseException(0, typeString + " [" + typeName + "] already exists in module " + type.getOwnerModule().getModule());
        }
    }
}
