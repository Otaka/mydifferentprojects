package com.simplepl.exception;

import com.simplepl.grammar.ast.Ast;

/**
 * @author Dmitry
 */
public class ParseException extends RuntimeException{
    private int position;

    public ParseException(int position, String message) {
        super(message);
        this.position = position;
    }
    
    public ParseException(Ast ast, String message) {
        super(message);
    }
    
}
