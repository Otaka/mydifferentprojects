package com.simplepl.grammar;

import com.simplepl.grammar.matchers.JavaUnicodeMatcherStartString;
import com.simplepl.grammar.matchers.JavaUnicodeMatcherString;
import org.parboiled.Rule;

/**
 * @author Dmitry
 */
public class MainParser extends MainParserActions {

    public Rule main() {
        return Sequence(ZeroOrMore(line()), EOI);
    }

    public Rule line() {
        return expression();
    }

    public Rule functionRule() {
        return Sequence(
                functionDeclaration(),
                FirstOf(
                        expressionsBlock(),
                        actionFail("Expected function body {...} after function declaration")
                )
        );
    }

    public Rule testFunctionRule() {
        return Sequence(functionRule(), EOI);
    }

    public Rule expressionsBlock() {
        return Sequence(
                openCurleyBracket(),
                FirstOf(
                        codeBlockExpressions(),
                        actionFail("Expected expressions in the expression block")
                ),
                FirstOf(
                        closeCurleyBracket(),
                        actionFail("Expected '}' to close the block")
                )
        );
    }

    public Rule codeBlockExpressions() {
        return ZeroOrMore(
                Sequence(
                        expression(),
                        expectedSemicolon()
                )
        );
    }

    public Rule expectedSemicolon() {
        return FirstOf(
                semicolon(),
                actionFail("Expected ';' after the expression")
        );
    }

    public Rule expressionsSeparatedWithComma() {
        return Optional(
                Sequence(
                        expression(),
                        ZeroOrMore(
                                comma(),
                                expression()
                        )
                )
        );
    }

    public Rule expression() {
        return FirstOf(
                breakStatement(),
                continueStatement(),
                forStatement(),
                whileStatement(),
                ifStatement(),
                newStatement(),
                deleteStatement(),
                functionRule(),
                structure(),
                declareArray(),
                declareVariableAndAssign(),
                declareVariable(),
                cast(),
                checkExpression()
        );
    }

    public Rule cast() {
        return Sequence(
                openCornerBracket(),
                typeIdentifier(),
                closeCornerBracket(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression to cast after the <Type>")
                )
        );
    }

    public Rule ifStatement() {
        return Sequence(
                keyword(IF),
                FirstOf(
                        openBracket(),
                        actionFail("Expected if condition '(...)'")
                ),
                FirstOf(
                        expression(),
                        actionFail("Expected expression in the if condition")
                ),
                FirstOf(
                        closeBracket(),
                        actionFail("Expected ')' after the if condition")
                ),
                FirstOf(
                        expressionsBlock(),
                        actionFail("Expected expression block for 'if'")
                ),
                Optional(
                        keyword(ELSE),
                        FirstOf(
                                expressionsBlock(),
                                ifStatement(),
                                actionFail("Expected 'else' code block after 'else keyword, or next chained if'")
                        )
                )
        );
    }

    public Rule whileStatement() {
        return Sequence(
                keyword(WHILE),
                FirstOf(openBracket(), actionFail("Expected open bracket for condition after 'while'")),
                FirstOf(checkExpression(), actionFail("Expected condition")),
                FirstOf(closeBracket(), actionFail("Expected ')' after condition")),
                FirstOf(expressionsBlock(), actionFail("Expected code block {...} after the while(condition)"))
        );
    }

    public Rule forStatement() {
        return Sequence(
                keyword(FOR),
                FirstOf(openBracket(), actionFail("Expected open bracket for condition after 'for'")),
                FirstOf(expression(), actionFail("Expected variable initialization expression")),
                FirstOf(semicolon(), actionFail("Expected first ';' in for(init;check;increment)")),
                FirstOf(expression(), actionFail("Expected check condition in 'for' initialization")),
                FirstOf(semicolon(), actionFail("Expected second ';' in for(init;check;increment)")),
                FirstOf(expression(), actionFail("Expected iteration expression in 'for' initialization")),
                FirstOf(closeBracket(), actionFail("Expected closing bracket for initialization of 'for'")),
                FirstOf(expressionsBlock(), actionFail("Expected code block {...} after the for(init;check;increment)"))
        );
    }

    public Rule newStatement() {
        return Sequence(
                keyword(NEW),
                FirstOf(
                        typeIdentifier(),
                        actionFail("Expected type after 'new'")
                )
        );
    }

    public Rule deleteStatement() {
        return Sequence(
                keyword(DELETE),
                FirstOf(
                        variable(),
                        actionFail("Expected pointer after 'delete'")
                )
        );
    }

    public Rule continueStatement() {
        return keyword(CONTINUE);
    }

    public Rule breakStatement() {
        return keyword(BREAK);
    }

    public Rule testFunctionCall() {
        return Sequence(functionCall(), EOI);
    }

    public Rule functionCall() {
        return Sequence(
                identifier(),
                openBracket(),
                expressionsSeparatedWithComma(),
                closeBracket(),
                Optional(
                        extensionExpressionBlock()
                )
        );
    }

    public Rule extensionExpressionBlock() {
        return Sequence(
                Optional(
                        extensionArgumentRename()
                ),
                expressionsBlock()
        );
    }

    public Rule extensionArgumentRename() {
        return Sequence(
                identifier(),
                ZeroOrMore(
                        comma(),
                        FirstOf(
                                identifier(),
                                actionFail("Expected identifier for the renamed argument")
                        )
                )
        );
    }

    public Rule functionDeclaration() {
        return Sequence(
                keyword(FUN),
                FirstOf(
                        identifier(),
                        actionFail("Expected function name")
                ),
                _pushAst_ExtractTopStringObjAndSetAsAttribute("functionDeclaration", "name"),
                functionArgumentWithBrackets(),
                functionExtensionDeclaration()
        );
    }

    public Rule functionArgumentWithBrackets() {
        return Sequence(
                FirstOf(
                        openBracket(),
                        actionFail("Expected open bracket of function arguments")
                ),
                _pushAst("function_arguments"),
                argumentList(),
                FirstOf(
                        closeBracket(),
                        actionFail("Expected close bracket")
                )
        );
    }

    public Rule argumentList() {
        return Optional(
                Sequence(
                        declareVariable(),
                        ZeroOrMore(
                                comma(),
                                FirstOf(
                                        declareVariable(),
                                        actionFail("Expected argument declaration")
                                )
                        )
                )
        );
    }

    public Rule functionExtensionDeclaration() {
        return Optional(
                Sequence(
                        keyword(EXTENSION),
                        functionArgumentWithBrackets()
                )
        );
    }

    public Rule testDeclareVariable() {
        return Sequence(declareVariable(), EOI);
    }

    public Rule declareVariable() {
        return Sequence(
                typeIdentifier(),
                identifier(),
                _pushAst_ExtractTopAstAndSetAsAttribute("var", "name","type")
        );
    }

    public Rule declareVariableWithSemicolon() {
        return Sequence(declareVariable(), semicolon());
    }

    public Rule testDeclareArray() {
        return Sequence(declareArray(), EOI);
    }

    public Rule declareArray() {
        return Sequence(
                typeIdentifier(),
                arrayDeclarerSquares(),
                FirstOf(
                        identifier(),
                        actionFail("Expected variable name of the array")
                )
        );
    }

    public Rule declareVariableAndAssign() {
        return Sequence(
                declareVariable(),
                assign(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression assigned to variable")
                )
        );
    }

    public Rule arrayDeclarerSquares() {
        return OneOrMore(
                openSquareBracket(),
                FirstOf(
                        closeSquareBracket(),
                        actionFail("Expected ']' that closes array mark")
                )
        );
    }

    public Rule number() {
        return Sequence(
                Sequence(
                        Optional('-'),
                        OneOrMore(
                                Digit()
                        ),
                        Optional(
                                '.',
                                OneOrMore(
                                        Digit()
                                )
                        )
                ),
                possibleSpace()
        ).suppressSubnodes();
    }

    public Rule Digit() {
        return Sequence(
                possibleSpace(),
                OneOrMore(
                        CharRange('0', '9')
                ),
                possibleSpace()
        );
    }

    public Rule testGenericStringRule() {
        return Sequence(genericStringRule(), EOI);
    }

    public Rule genericStringRule() {
        return Sequence(
                '"',
                ZeroOrMore(
                        FirstOf(
                                Sequence(
                                        EOI,
                                        actionFail("Found end of line while reading string")
                                ),
                                Escape(),
                                Sequence(
                                        TestNot("\""),
                                        ANY
                                )
                        )
                ).suppressSubnodes(),
                '"'
        );
    }

    public Rule testRawStringRule() {
        return Sequence(rawStringRule(), EOI);
    }

    public Rule rawStringRule() {
        return Sequence(
                "\"\"\"",
                ZeroOrMore(
                        FirstOf(
                                Sequence(
                                        EOI,
                                        actionFail("Found end of line while reading string")
                                ),
                                Sequence(TestNot("\"\"\""), ANY)
                        )
                ).suppressSubnodes(),
                "\"\"\""
        );
    }

    Rule Escape() {
        return Sequence(
                '\\',
                AnyOf("btnfr\"\'\\")
        );
    }

    public Rule checkExpression() {
        return FirstOf(
                Sequence(
                        keyword("!"),
                        FirstOf(
                                checkExpression(),
                                actionFail("Expected expression after 'not'")
                        )
                ),
                Sequence(
                        equalRule(),
                        ZeroOrMore(
                                FirstOf(
                                        keyword("&&"),
                                        keyword("||")
                                ),
                                pushMatchString("and/or_operation"),
                                FirstOf(
                                        equalRule(),
                                        actionFail("after 'and'/'or' should be another expression")
                                )
                        )
                )
        );
    }

    public Rule equalRule() {
        return Sequence(
                sumRule(),
                ZeroOrMore(
                        FirstOf(keyword("!="), keyword("=="), keyword("="), keyword(">="), keyword("<="), keyword(">"), keyword("<")),
                        FirstOf(
                                sumRule(),
                                actionFail("Expected expression after comparing operation")
                        )
                )
        );
    }

    public Rule sumRule() {
        return Sequence(
                term(),
                ZeroOrMore(
                        FirstOf(keyword("+"), keyword("-")),
                        FirstOf(
                                term(),
                                actionFail("Expected expression after +/-")
                        )
                )
        );
    }

    public Rule term() {
        return Sequence(
                atom(),
                ZeroOrMore(
                        Sequence(
                                FirstOf(keyword("/"), keyword("*")),
                                pushMatchString("* or / operation")
                        ),
                        dbgPrint("multiplication term $match"),
                        FirstOf(
                                atom(),
                                actionFail("Expected expression after * or /")
                        )
                )
        );
    }

    public Rule atom() {
        return FirstOf(
                number(),
                booleanValueRule(),
                functionCall(),
                variable(),
                rawStringRule(),
                genericStringRule(),
                parens()
        );
    }

    public Rule variable() {
        return FirstOf(
                structVariable(),
                simpleVariable()
        );
    }

    public Rule simpleVariable() {
        return FirstOf(
                Sequence(
                        keyword(POINTER),
                        identifier()
                ),
                identifier()
        );
    }

    public Rule structVariable() {
        return Sequence(
                simpleVariable(),
                OneOrMore(
                        Sequence(
                                keyword("."),
                                simpleVariable()
                        )
                )
        );
    }

    public Rule booleanValueRule() {
        return FirstOf(
                keyword(TRUE),
                keyword(FALSE)
        );
    }

    public Rule parens() {
        return Sequence(
                openBracket(),
                FirstOf(
                        checkExpression(),
                        actionFail("Expected expression inside the (...)")
                ),
                FirstOf(
                        closeBracket(),
                        actionFail("Expected closing bracket")
                )
        );
    }

    public Rule testStructure() {
        return Sequence(structure(), EOI);
    }

    public Rule structure() {
        return Sequence(
                keyword(STRUCTURE),
                FirstOf(
                        identifier(),
                        actionFail("Expected structure name")
                ),
                FirstOf(
                        openCurleyBracket(),
                        actionFail("Expected '{' after structure name")
                ),
                FirstOf(
                        elementsOfStructure(),
                        actionFail("Expected structure elements inside structure body")
                ),
                FirstOf(
                        closeCurleyBracket(),
                        actionFail("Expected '}' at the end of the structure")
                )
        );
    }

    public Rule elementsOfStructure() {
        return Sequence(
                declareVariableWithSemicolon(),
                ZeroOrMore(
                        declareVariableWithSemicolon()
                )
        );
    }

    public Rule oneSpaceCharacter() {
        return FirstOf(' ', '\t', '\r', '\n');
    }

    public Rule ensureSpace() {
        return OneOrMore(
                oneSpaceCharacter()
        );
    }

    public Rule possibleSpace() {
        return ZeroOrMore(
                oneSpaceCharacter()
        );
    }

    public Rule identifier() {
        return Sequence(
                possibleSpace(),
                Sequence(
                        new JavaUnicodeMatcherStartString(),
                        ZeroOrMore(
                                new JavaUnicodeMatcherString()
                        )
                ),
                pushMatchString("identifier name"),
                possibleSpace()
        ).suppressSubnodes();
    }

    public Rule typeIdentifier() {
        return FirstOf(
                pointerIdentifier(),
                simpleIdentifier()
        );
    }

    public Rule pointerIdentifier() {
        return Sequence(
                identifier(),
                keyword(POINTER),
                _pushAst_ExtractTopStringObjAndSetAsAttribute("pointer", "type")
        );
    }

    public Rule simpleIdentifier() {
        return Sequence(
                identifier(),
                _pushAst_ExtractTopStringObjAndSetAsAttribute("type", "type")
        );
    }

    public Rule keyword(String val) {
        return Sequence(
                possibleSpace(),
                val,
                possibleSpace()
        ).suppressSubnodes();
    }

    public Rule openBracket() {
        return keyword("(");
    }

    public Rule openCornerBracket() {
        return keyword("<");
    }

    public Rule closeCornerBracket() {
        return keyword(">");
    }

    public Rule closeBracket() {
        return keyword(")");
    }

    public Rule openCurleyBracket() {
        return keyword("{");
    }

    public Rule closeCurleyBracket() {
        return keyword("}");
    }

    public Rule openSquareBracket() {
        return keyword("[");
    }

    public Rule closeSquareBracket() {
        return keyword("]");
    }

    public Rule assign() {
        return keyword("=");
    }

    public Rule semicolon() {
        return keyword(";");
    }

    public Rule colon() {
        return keyword(":");
    }

    public Rule comma() {
        return keyword(",");
    }

    public Rule STR_TERMINAL(char... character) {
        return Sequence(
                ZeroOrMore(
                        NoneOf(character)
                ),
                character
        );
    }

    public String TRUE = "true";
    public String FALSE = "false";
    public String EXTENSION = "extension";
    public String FUN = "fun";
    public String POINTER = "@";
    public String STRUCTURE = "structure";
    public String NEW = "new";
    public String DELETE = "delete";
    public String CONTINUE = "continue";
    public String BREAK = "break";
    public String IF = "if";
    public String ELSE = "else";
    public String WHILE = "while";
    public String FOR = "for";

}
