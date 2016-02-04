package com.macro;

import com.macro.exception.MacrosCompilationException;
import com.macro.functions.AbstrMacroFunction;
import com.macro.functions.AbstrMacroFunctionWithoutBrackets;
import com.macro.tokenizer.Token;
import com.macro.tokenizer.TokenType;
import com.macro.tokenizer.Tokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sad
 */
public class MacroProcessor {

    private final Map<String, AbstrMacroFunction> macroFunctions = new HashMap<>();

    public MacroProcessor() {
    }

    public void addMacroFunction(String macroFunction, AbstrMacroFunction function) {
        macroFunctions.put(macroFunction, function);
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
                    String macroResult = processMacro(word, tokenizer);
                    result.append(macroResult);
                    int startMacroLine = t.getLine();
                    int endLine = tokenizer.getLineNumber();
                    if (endLine != startMacroLine) {
                        appendMissingNewLines(result, endLine - startMacroLine);
                    }
                } else {
                    result.append(word);
                }
            }
        }

        return result.toString();
    }

    private void appendMissingNewLines(StringBuilder sb, int newLinesCount) {
        for(int i=0;i<newLinesCount;i++){
            sb.append("\n");
        }
    }

    private boolean isOpenBracket(Token t) {
        return t.getValue().equals("(");
    }

    private String processMacro(String macroFunction, Tokenizer tokenizer) {
        AbstrMacroFunction function = getMacroFunction(macroFunction);
        if (function instanceof AbstrMacroFunctionWithoutBrackets) {
            Token t = tokenizer.nextTokenSkipComment();
            if (t == null) {
                throw new MacrosCompilationException("Find end of input while try to find argument for macrofunction [" + macroFunction + "]");
            }

            if (isOpenBracket(t) || t.getTokenType() == TokenType.Space) {
                throw new MacrosCompilationException("Macrofunction [" + macroFunction + "] is macro function withot bracket. It is should be followed by the argument immideately, without spaces");
            }

            String result = function.process(Arrays.asList(t.getValue()));
            return result;
        } else {
            Token t = tokenizer.nextTokenSkipSpaceAndComments();
            if (!isOpenBracket(t)) {
                throw new MacrosCompilationException("Macrofunction [" + macroFunction + "] should be followed by the open bracket with arguments");
            }

            List<String> args = parseArgumentList(macroFunction, tokenizer);
            String result = function.process(args);
            return result;
        }
    }

    private List<String> parseArgumentList(String macroFunction, Tokenizer tokenizer) {
        List<String> args = new ArrayList<String>();
        while (true) {
            Token t = tokenizer.nextTokenSkipSpaceAndComments();
            if (t == null) {
                throw new MacrosCompilationException("Found end of input while trying to find end ')' for macro function [" + macroFunction + "]");
            }

            String textValue = t.getValue();
            if (textValue.equals(")")) {
                break;
            } else if (isMacroFunction(textValue)) {
                textValue = processMacro(textValue, tokenizer);
                args.add(textValue);
            } else if (textValue.equals(",")) {
                throw new MacrosCompilationException("Found ',' but try to find argument for macro function [" + macroFunction + "]");
            } else {
                args.add(textValue);
            }

            t = tokenizer.nextTokenSkipSpaceAndComments();
            if (t == null) {
                throw new MacrosCompilationException("Found end of input while trying to find end ')' for macro function [" + macroFunction + "]");
            }

            textValue = t.getValue();
            if (textValue.equals(",")) {
                continue;
            } else if (textValue.equals(")")) {
                break;
            } else {
                throw new MacrosCompilationException("Found [" + textValue + "] while search ',' or ')' in arguments for macro function [" + macroFunction + "]");
            }
        }

        return args;
    }
}
