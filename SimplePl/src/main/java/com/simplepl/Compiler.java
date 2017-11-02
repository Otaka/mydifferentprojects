package com.simplepl;


import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class Compiler {

    private Context context;
   // private Map<String, CompiledModule> compiledModules = new HashMap<>();

    public Compiler(Context context) {
        this.context = context;
    }

    public void compileProgram(String module) {
        //compileModule(module);
    }

   /* public CompiledModule compileModule(String module) {
        if (isAlreadyCompiled(module)) {
            return getAlreadyCompiledModule(module);
        }

        ModuleInfo moduleInfo = context.getAstManager().getModuleInfo(module);
        //context.getTypeManager().gatherTypesFromModule(moduleInfo);
        CompiledModule compiledModule = new CompiledModule();
        addToAlreadyCompiledModules(module, compiledModule);
        return compiledModule;
    }*/

    /*private boolean isAlreadyCompiled(String module) {
        return getAlreadyCompiledModule(module) != null;
    }*/

    /*private CompiledModule getAlreadyCompiledModule(String module) {
        return compiledModules.get(module);
    }

    private void addToAlreadyCompiledModules(String module, CompiledModule compiledModule) {
        compiledModules.put(module, compiledModule);
    }*/

}
