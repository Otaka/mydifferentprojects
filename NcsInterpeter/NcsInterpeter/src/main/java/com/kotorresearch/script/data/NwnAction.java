package com.kotorresearch.script.data;

import com.kotorresearch.script.interpreter.ScriptEvaluator;

/**
 * @author Dmitry
 */
public class NwnAction {

    private ScriptEvaluator parentScriptEvaluator;
    private ScriptEvaluator closureScriptEvaluator;

    public NwnAction(ScriptEvaluator parentScriptEvaluator, ScriptEvaluator closureScriptEvaluator) {
        this.parentScriptEvaluator = parentScriptEvaluator;
        this.closureScriptEvaluator = closureScriptEvaluator;
    }

    public ScriptEvaluator getClosureScriptEvaluator() {
        return closureScriptEvaluator;
    }

    public ScriptEvaluator getParentScriptEvaluator() {
        return parentScriptEvaluator;
    }

}
