package com.macro;

import com.macro.functions.AbstrMacroFunction;
import com.macro.tokenizer.Token;
import com.macro.tokenizer.TokenType;
import com.macro.tokenizer.Tokenizer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class MacroProcessor {

    private final Map<String, AbstrMacroFunction> macroFunctions = new HashMap<>();

    public MacroProcessor() {
    }

    public void addMacroFunction(String macroFunction, AbstrMacroFunction function) {

    }

    private AbstrMacroFunction getMacroFunction(String name) {
        return macroFunctions.get(name);
    }

    private boolean isMacroFunction(String name) {
        return macroFunctions.get(name) != null;
    }

    public String process(String text) {
        StringBuilder result = new StringBuilder(text.length());
        Tokenizer tokenizer = new Tokenizer(text);
        while (true) {
            Token t = tokenizer.nextToken();
            if (t == null) {
                break;
            }

            if (t.getTokenType() != TokenType.Data) {
                result.append(t.getValue());
            } else {
                String word = t.getValue();
                if (isMacroFunction(word)) {

                } else {
                    result.append(word);
                }
            }
        }

        return result.toString();
    }

}
