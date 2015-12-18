package com.macro;

import com.macro.tokenizer.Token;
import com.macro.tokenizer.TokenType;
import com.macro.tokenizer.Tokenizer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class MacroProcessor {

    private final Map<String, AbstrMacroFunction> macrofunctions = new HashMap<>();

    public MacroProcessor() {
    }

    public void addMacroFunction(String macroFunction, AbstrMacroFunction function) {

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
            }else{
                String word=t.getValue();
            }
        }

        return result.toString();
    }

}
