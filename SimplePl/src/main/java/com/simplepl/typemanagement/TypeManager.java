package com.simplepl.typemanagement;

import com.simplepl.astinfoextractor.PublicEntitiesExtractor;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.grammar.ast.Ast;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class TypeManager {

    private Map<String, ModuleInfo> moduleInfoCache = new HashMap<>();
    private ModuleAstProvider moduleAstProvider;

    public void setModuleAstProvider(ModuleAstProvider moduleAstProvider) {
        this.moduleAstProvider = moduleAstProvider;
    }

    public ModuleInfo getModuleInfo(String modulePath) {
        ModuleInfo moduleInfo = moduleInfoCache.get(modulePath);
        if (moduleInfo != null) {
            return moduleInfo;
        }

        Ast moduleAst=moduleAstProvider.parseModule(modulePath);
        PublicEntitiesExtractor extractor = new PublicEntitiesExtractor();
        moduleInfo=extractor.processAst(moduleAst, modulePath);
        moduleInfoCache.put(modulePath, moduleInfo);
        return moduleInfo;
    }
}
