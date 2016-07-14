package com.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ASTCompiler {

    private ParserInputStream stream;

    public Module parseSource(String value) {
        stream = new ParserInputStream(value);
        stream.skipBlank();

        List<SExpression> expressions = new ArrayList<>();
        while (true) {
            stream.skipBlank();
            if(stream.eof()){
                break;
            }
            SExpression moduleExpression = parseSExpression('(',')');
            if (moduleExpression == null) {
                break;
            }

            expressions.add(moduleExpression);
        }

        Module module = new Module(expressions);
        return module;
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    private StringExpression parseStringExpression() {
        return new StringExpression(stream.readQuotedString());
    }

    private TokenExpression parseTokenExpression() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = stream.peek();
            if (c == '(' || isWhitespace(c) || c == ')' || c == '[' || c == ']') {
                break;
            }
            if (c == '/') {
                char nextC = stream.peek(1);
                if (nextC == '/' || nextC == '*') {
                    break;
                }
            }

            stream.next();
            sb.append(c);
        }

        return new TokenExpression(sb.toString());
    }

    private NumberExpression parseNumberExpression() {
        char c = stream.peek(0);
        boolean negative = false;
        if (c == '-') {
            negative = true;
            stream.next();
        }

        long value = 0;
        while (true) {
            c = stream.peek();
            if (c >= '0' && c <= '9') {
                value = value * 10 + (c - '0');
                stream.next();
            } else if (c == '.') {
                throw new ParserException("Parser currently not support floating point number", stream.getCurrentLine(), stream.getOffset());
            } else if (c == ')' || c == ']') {
                break;
            } else if (!isWhitespace(c)) {
                throw new ParserException("Number should end with space", stream.getCurrentLine(), stream.getOffset());
            } else {
                break;
            }
        }

        if (negative) {
            value = -1 * value;
        }

        return new INumberExpression(value);
    }

    private SExpression parseSExpression(char openBracketSymbol,char closeBracketSymbol) {
        char c = stream.peek();
        if (c != openBracketSymbol) {
            throw new ParserException("SExpression should start from '"+openBracketSymbol+"'", stream.getCurrentLine(), stream.getOffset());
        }

        stream.next();
        List<Expression> expressions = new ArrayList<>();
        outer:
        while (true) {
            stream.skipBlank();
            c = stream.peek();
            if (c == ParserInputStream.NO_CHAR) {
                throw new ParserException("Reached end of source file while parsing SExpression", stream.getCurrentLine(), stream.getOffset());
            }
            Expression expression=null;
            if (c == closeBracketSymbol) {
                stream.next();
                break;
            } else if (c == '(') {
                expression=parseSExpression('(',')');
            } else if (c == '[') {
                expression=parseSExpression('[',']');
            } else if (c >= '0' && c <= '9') {
                expression=parseNumberExpression();
            } else if (c >= '-' && (stream.peek() >= '0' && stream.peek(0) <= '9')) {
                expression=parseNumberExpression();
            } else if (c == '\"') {
                expression=parseStringExpression();
            } else {
                expression=parseTokenExpression();
            }
            if(expression!=null){
                expressions.add(expression);
            }
        }

        SExpression expression = new SExpression(expressions);
        expression.setBracketSymbol(openBracketSymbol);
        return expression;
    }
}
