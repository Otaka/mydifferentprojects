package com.simplepl.intermediate;

import com.simplepl.entity.ModuleInfo;

/**
 * @author sad
 */
public class BytecodeModule {
    private ModuleInfo moduleInfo;

    public BytecodeModule(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }
    
}
