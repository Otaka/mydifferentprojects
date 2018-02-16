package com.expressionlayout.interpreter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sad
 */
public class Interpreter {

    private Pattern expressionSplitPattern = Pattern.compile("^;");
    private Pattern whitespacePattern = Pattern.compile("^\\s*");

    private Pattern numberPattern = Pattern.compile("^(?<beforedot>\\d+)(?<afterdot>\\.\\d+)?(?<suffix>[a-zA-Z_%]*)");//number with potential suffix like 10% or 10pt or 10px
    private Pattern variablePattern = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*");
    private Pattern variableWithFieldAccessPattern = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*\\.[_a-zA-Z][_a-zA-Z0-9]*");
    private Pattern operationPattern = Pattern.compile("^[\\*\\/\\+\\-\\<\\>]");
    private Pattern openBracketPattern = Pattern.compile("^\\(");
    private Pattern closeBracketPattern = Pattern.compile("^\\)");
    private Pattern assignPattern = Pattern.compile("^\\=");

    // Associativity constants for operators
    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;
    public List<VariableResolver> variableResolvers = new ArrayList<>();

    private static final Map<String, int[]> operatorPrecedenceMap = new HashMap<String, int[]>();

    static {
        operatorPrecedenceMap.put("+", new int[]{0, LEFT_ASSOC});
        operatorPrecedenceMap.put("-", new int[]{0, LEFT_ASSOC});
        operatorPrecedenceMap.put("*", new int[]{5, LEFT_ASSOC});
        operatorPrecedenceMap.put("/", new int[]{5, LEFT_ASSOC});
    }

    public void addVariableResolver(VariableResolver variableResolver) {
        variableResolvers.add(variableResolver);
    }

    public NumberObject execute(CompiledExpression compiledExpression) {
        NumberObject numberObject = null;
        for (int i = 0; i < compiledExpression.getSubexpressions().size(); i++) {
            numberObject = executeSubExpression(compiledExpression.getSubexpressions().get(i));
        }
        return numberObject;
    }

    private NumberObject executeSubExpression(CompiledSubExpression expression) {
        List<NumberObject> stack = new ArrayList<NumberObject>(100);
        for (int i = 0; i < expression.getPolishNotationTokens().size(); i++) {
            Token token = expression.getPolishNotationTokens().get(i);

            if (token.getType() == TokenType.variable) {
                NumberObject no = getVariable(token.getValue(), null);
                stack.add(no);
            } else if (token.getType() == TokenType.variable_field_access) {
                String variableNameWithField = token.getValue();
                int dotIndex = variableNameWithField.indexOf('.');
                String variableName = variableNameWithField.substring(0, dotIndex);
                String fieldName = variableNameWithField.substring(dotIndex + 1);

                NumberObject no = getVariable(variableName, fieldName);
                stack.add(no);
            } else if (token.getType() == TokenType.number) {
                NumberObject no=((NumberObject) token.getUserObject()).makeClone();
                no=processNumberSuffix(no);
                stack.add(no);
            } else if (token.getType() == TokenType.operation) {
                NumberObject rhs = stack.remove(stack.size() - 1);
                NumberObject lhs = stack.remove(stack.size() - 1);
                switch (token.getValue()) {
                    case "+":
                        lhs.add(rhs);
                        break;
                    case "-":
                        lhs.minus(rhs);
                        break;
                    case "*":
                        lhs.mul(rhs);
                        break;
                    case "/":
                        lhs.div(rhs);
                        break;
                }

                stack.add(lhs);
            } else {
                throw new IllegalStateException("Unexpected token type [" + token.getType() + "]");
            }
        }

        NumberObject no = stack.get(0);
        if (expression.getAssignTo() != null) {
            Token token = expression.getAssignTo();
            if (token.getType() == TokenType.variable) {
                setVariable(token.getValue(), null, no);
            } else if (token.getType() == TokenType.variable_field_access) {
                String variableNameWithField = token.getValue();
                int dotIndex = variableNameWithField.indexOf('.');
                String variableName = variableNameWithField.substring(0, dotIndex);
                String fieldName = variableNameWithField.substring(dotIndex + 1);
                setVariable(variableName, fieldName, no);
            }
        }

        return no;
    }

    private NumberObject processNumberSuffix(NumberObject no) {
        if (no.getSuffix() == null) {
            return no;
        }
        String originalSuffix = no.getSuffix();
        int loopCount = 100;
        for (int j = 0; j < loopCount; j++) {
            NumberObject processedNumber = null;
            for (int i = 0; i < variableResolvers.size(); i++) {
                VariableResolver resolver = variableResolvers.get(i);
                processedNumber = resolver.suffixResolver(no);
                if (processedNumber != null) {
                    break;
                }
            }

            if (processedNumber == null) {
                throw new IllegalStateException("Illegal suffix [" + no.getSuffix() + "]. Cannot find handler.");
            }

            no = processedNumber;
            if (no.getSuffix() == null) {
                return no;
            }
        }

        throw new IllegalArgumentException("Made [" + loopCount + "] iteration while try to resolve suffix [" + originalSuffix + "] but it is not resolved. It seems the problem in variableResolver.");
    }

    public CompiledExpression compileExpression(String expressionString) {
        List<List<Token>> splittedTokens = splitStringToTokens(expressionString);

        List<CompiledSubExpression> subExpressions = new ArrayList<>();
        for (int i = 0; i < splittedTokens.size(); i++) {
            List<Token> tokens = splittedTokens.get(i);
            CompiledSubExpression compiledSubExpression = compileSubExpression(tokens);
            subExpressions.add(compiledSubExpression);
        }

        CompiledExpression expression = new CompiledExpression(subExpressions);
        return expression;
    }

    private List<List<Token>> splitStringToTokens(String source) {
        TokenType[] operationsCodes = new TokenType[]{
            TokenType.openBracket,
            TokenType.closeBracket,
            TokenType.number,
            TokenType.operation,
            TokenType.variable_field_access,
            TokenType.variable,
            TokenType.assign,};
        List<Pattern> patternsToApply = new ArrayList<>();
        patternsToApply.add(openBracketPattern);
        patternsToApply.add(closeBracketPattern);
        patternsToApply.add(numberPattern);
        patternsToApply.add(operationPattern);
        patternsToApply.add(variableWithFieldAccessPattern);
        patternsToApply.add(variablePattern);
        patternsToApply.add(assignPattern);
        List<List<Token>> expressionsList = new ArrayList<>();
        List<Token> currentTokensList = new ArrayList<>();
        while (!source.isEmpty()) {
            source = stripWhitespace(source);
            String newExpressionString = matchPattern(source, expressionSplitPattern);
            if (newExpressionString != null) {
                source = removePrefix(source, newExpressionString);
                if (!currentTokensList.isEmpty()) {
                    expressionsList.add(currentTokensList);
                    currentTokensList = new ArrayList<>();
                }
            }

            source = stripWhitespace(source);
            for (int i = 0; i < patternsToApply.size(); i++) {
                Pattern pattern = patternsToApply.get(i);
                String matchedString = matchPattern(source, pattern);
                if (matchedString != null) {
                    Token token = new Token(matchedString, operationsCodes[i]);
                    if (token.getType() == TokenType.number) {
                        preprocessNumberToken(token);
                    }

                    currentTokensList.add(token);
                    source = removePrefix(source, matchedString);
                    break;
                }
            }
        }

        if (!currentTokensList.isEmpty()) {
            expressionsList.add(currentTokensList);
            currentTokensList = new ArrayList<>();
        }

        return expressionsList;
    }

    private void preprocessNumberToken(Token numberToken) {
        Matcher matcher = numberPattern.matcher(numberToken.getValue());
        if (!matcher.matches()) {
            throw new IllegalStateException("Something wrong. Token [" + numberToken + "] is not matched with numberPattern matcher");
        }
        String beforeDot = matcher.group("beforedot");
        String afterDot = matcher.group("afterdot");
        String suffix = matcher.group("suffix");
        NumberObject numberObject;
        if (afterDot != null) {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number;
            String numberString = beforeDot + afterDot;
            try {
                number = format.parse(numberString);
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Cannot parse number [" + numberString + "] as double");
            }

            double d = number.doubleValue();
            numberObject = new NumberObject(d);
        } else {
            numberObject = new NumberObject(Long.parseLong(beforeDot));
        }

        numberObject.setSuffix(suffix);
        numberToken.setUserObject(numberObject);
    }

    private boolean isOperator(Token token) {
        return token.getType() == TokenType.operation;
    }

    private int cmpPrecedence(Token token1, Token token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: " + token1 + " " + token2);
        }
        return operatorPrecedenceMap.get(token1.getValue())[0] - operatorPrecedenceMap.get(token2.getValue())[0];
    }

    private List<Token> infixToReversePolishNotation(List<Token> inputTokens) {
        ArrayList<Token> out = new ArrayList<>();
        Stack<Token> stack = new Stack<>();
        for (Token token : inputTokens) {
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) && cmpPrecedence(
                            token, stack.peek()) <= 0)
                            || (isAssociative(token, RIGHT_ASSOC) && cmpPrecedence(
                            token, stack.peek()) < 0)) {
                        out.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            } else if (token.getType() == TokenType.openBracket) {
                stack.push(token);
            } else if (token.getType() == TokenType.closeBracket) {

                while (!stack.empty() && stack.peek().getType() != TokenType.openBracket) {
                    out.add(stack.pop());
                }

                stack.pop();
            } else {
                out.add(token);
            }
        }

        while (!stack.empty()) {
            out.add(stack.pop());
        }

        List<Token> output = new ArrayList<>();
        output.addAll(out);
        return output;
    }

    private boolean isAssociative(Token token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
        return operatorPrecedenceMap.get(token.getValue())[1] == type;
    }

    private String stripWhitespace(String value) {
        String whiteSpace = matchPattern(value, whitespacePattern);
        return removePrefix(value, whiteSpace);
    }

    private String removePrefix(String value, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return value;
        }
        return value.substring(prefix.length());
    }

    private String matchPattern(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private CompiledSubExpression compileSubExpression(List<Token> tokens) {
        if (tokens.size() < 3 || tokens.get(1).getType() != TokenType.assign) {
            throw new CompileException("Expression should have the structure [" + TokenType.variable + " " + TokenType.assign + " expression], but you have [" + expressionToStringStructure(tokens) + "]");
        }

        Token leftPart = tokens.get(0);
        if (!(leftPart.getType() == TokenType.variable || leftPart.getType() == TokenType.variable_field_access)) {
            throw new CompileException("Left part of the expression should be [" + TokenType.variable + "] or [" + TokenType.variable_field_access + "] but it is [" + leftPart + "]");
        }

        List<Token> polishNotationTokens = infixToReversePolishNotation(tokens.subList(2, tokens.size()));
        CompiledSubExpression compiledExpression = new CompiledSubExpression(leftPart, polishNotationTokens);
        return compiledExpression;
    }

    private String expressionToStringStructure(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Token token : tokens) {
            if (first == false) {
                sb.append(" ");
            } else {
                first = false;
            }

            sb.append(token.getType());
        }

        return sb.toString();
    }

    private NumberObject getVariable(String name, String field) {
        for (int i = 0; i < variableResolvers.size(); i++) {
            VariableResolver vr = variableResolvers.get(i);
            if (field == null) {
                NumberObject value = vr.getVariable(name);
                if (value != null) {
                    return  processNumberSuffix(value);
                }
            } else {
                NumberObject value = vr.getVariableField(name, field);
                if (value != null) {
                    return processNumberSuffix(value);
                }
            }
        }

        throw new IllegalArgumentException("Unknown variable [" + name + "]");
    }

    private void setVariable(String name, String field, NumberObject no) {
        for (int i = 0; i < variableResolvers.size(); i++) {
            VariableResolver vr = variableResolvers.get(i);
            if (field == null) {
                if (vr.setVariable(name, no)) {
                    return;
                }
            } else {
                if (vr.setVariableField(name, field, no)) {
                    return;
                }
            }
        }

        throw new IllegalArgumentException("Unknown variable [" + name + "]");
    }
}
