package com.simplepl.intermediate;

import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;

/**
 * @author sad
 */
public class IntermediateCompiler {

    public BytecodeModule compile(Context context, ModuleInfo moduleInfo) {
        BytecodeModule bytecodeModule = new BytecodeModule(moduleInfo);

        
        return bytecodeModule;
    }
}
