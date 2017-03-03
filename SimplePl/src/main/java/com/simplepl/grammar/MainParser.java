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
                ),
                _pushTopStackAstToNextStackAstAsChild("expressions_list", "function")
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
        return Sequence(
                _pushAst("expressions_list"),
                ZeroOrMore(
                        Sequence(
                                Sequence(expression(), _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "expressions_list")),//?????? last UNKNOWN should be replaced to "expressions_list"
                                expectedSemicolon()
                        )
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
        return Sequence(
                _pushAst("expressions_list"),
                Optional(
                        Sequence(
                                Sequence(expression(), _pushTopStackAstToNextStackAstAsChild(UNKNOWN, UNKNOWN)),////?????? last UNKNOWN should be replaced to "expressions_list"
                                ZeroOrMore(
                                        comma(),
                                        Sequence(expression(), _pushTopStackAstToNextStackAstAsChild(UNKNOWN, UNKNOWN))//?????? last UNKNOWN should be replaced to "expressions_list"
                                )
                        )
                ));
    }

    public Rule testExpression() {
        return Sequence(expression(), EOI);
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
                _pushAst("type_conversion"),
                typeIdentifier(),
                _pushTopStackAstToNextStackAstAsAttribute("to_type", UNKNOWN, "type_conversion"),
                closeCornerBracket(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression to cast after the <Type>")
                ),
                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "type_conversion")
        );
    }

    public Rule ifStatement() {
        return Sequence(
                keyword(IF),
                _pushAst("if"),
                FirstOf(openBracket(), actionFail("Expected if condition '(...)'")),
                FirstOf(expression(), actionFail("Expected expression in the if condition")),
                _pushTopStackAstToNextStackAstAsAttribute("if_condition", UNKNOWN, "if"),
                FirstOf(closeBracket(), actionFail("Expected ')' after the if condition")),
                FirstOf(expressionsBlock(), actionFail("Expected expression block for 'if'")),
                _pushTopStackAstToNextStackAstAsChild("expressions_list", "if"),
                Optional(
                        keyword(ELSE),
                        Sequence(
                                FirstOf(
                                        expressionsBlock(),
                                        ifStatement(),
                                        actionFail("Expected 'else' code block after 'else keyword, or next chained if'")
                                ),
                                _pushTopStackAstToNextStackAstAsAttribute("else", UNKNOWN, "if")
                        )
                )
        );
    }

    public Rule whileStatement() {
        return Sequence(
                keyword(WHILE),
                _pushAst("while_loop"),
                FirstOf(openBracket(), actionFail("Expected open bracket for condition after 'while'")),
                FirstOf(expression(), actionFail("Expected condition")),
                _pushTopStackAstToNextStackAstAsAttribute("while_codition_expression", UNKNOWN, "while_loop"),
                FirstOf(closeBracket(), actionFail("Expected ')' after condition")),
                FirstOf(expressionsBlock(), actionFail("Expected code block {...} after the while(condition)")),
                _pushTopStackAstToNextStackAstAsChild("expressions_list", "while_loop")
        );
    }

    public Rule forStatement() {
        return Sequence(
                keyword(FOR),
                _pushAst("for_loop"),
                FirstOf(openBracket(), actionFail("Expected open bracket for condition after 'for'")),
                FirstOf(expression(), actionFail("Expected variable initialization expression")),
                _pushTopStackAstToNextStackAstAsAttribute("for_init_expression", UNKNOWN, "for_loop"),
                FirstOf(semicolon(), actionFail("Expected first ';' in for(init;check;increment)")),
                FirstOf(expression(), actionFail("Expected check condition in 'for' initialization")),
                _pushTopStackAstToNextStackAstAsAttribute("for_init_condition", UNKNOWN, "for_loop"),
                FirstOf(semicolon(), actionFail("Expected second ';' in for(init;check;increment)")),
                FirstOf(expression(), actionFail("Expected iteration expression in 'for' initialization")),
                _pushTopStackAstToNextStackAstAsAttribute("for_init_increment", UNKNOWN, "for_loop"),
                FirstOf(closeBracket(), actionFail("Expected closing bracket for initialization of 'for'")),
                FirstOf(expressionsBlock(), actionFail("Expected code block {...} after the for(init;check;increment)")),
                _pushTopStackAstToNextStackAstAsChild("expressions_list", "for_loop")
        );
    }

    public Rule newStatement() {
        return Sequence(
                keyword(NEW),
                _pushAst("new"),
                FirstOf(
                        typeIdentifier(),
                        actionFail("Expected type after 'new'")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("object_type_to_allocate", UNKNOWN, "new")
        );
    }

    public Rule deleteStatement() {
        return Sequence(
                keyword(DELETE),
                _pushAst("delete"),
                FirstOf(
                        variable(),
                        actionFail("Expected pointer after 'delete'")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("object_to_delete", UNKNOWN, "delete")
        );
    }

    public Rule continueStatement() {
        return Sequence(keyword(CONTINUE), _pushAst("continue"));
    }

    public Rule breakStatement() {
        return Sequence(keyword(BREAK), _pushAst("break"));
    }

    public Rule testFunctionCall() {
        return Sequence(functionCall(), EOI);
    }

    public Rule functionCall() {
        return Sequence(
                _pushAst("function_call"),
                identifier(),
                _pushTopStackAstToNextStackAstAsAttribute("name", "identifier", "function_call"),
                openBracket(),
                expressionsSeparatedWithComma(),
                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "function_call"),////?????? first UNKNOWN should be replaced to "expressions_list"
                closeBracket(),
                Optional(
                        Sequence(extensionExpressionBlock(), _pushTopStackAstToNextStackAstAsChild("function_extension", "function_call"))
                )
        );
    }

    public Rule extensionExpressionBlock() {
        return Sequence(
                _pushAst("function_extension"),
                Optional(
                        extensionArgumentRename(),
                        _pushTopStackAstToNextStackAstAsAttribute("argument_rename", "extension_arg_rename", "function_extension")
                ),
                expressionsBlock(),
                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "function_extension")//?????? first UNKNOWN should be replaced to "expressions_list"
        );
    }

    public Rule extensionArgumentRename() {
        return Sequence(
                _pushAst("extension_arg_rename"),
                Sequence(identifier(), _pushTopStackAstToNextStackAstAsChild("identifier", "extension_arg_rename")),
                ZeroOrMore(
                        comma(),
                        FirstOf(
                                Sequence(identifier(), _pushTopStackAstToNextStackAstAsChild("identifier", "extension_arg_rename")),
                                actionFail("Expected identifier for the renamed argument")
                        )
                )
        );
    }

    public Rule functionDeclaration() {
        return Sequence(
                keyword(FUN),
                _pushAst("function"),
                FirstOf(
                        identifier(),
                        actionFail("Expected function name")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("name", "identifier", "function"),
                functionArgumentWithBrackets(),
                _pushTopStackAstToNextStackAstAsAttribute("arguments", "function_arguments", "function"),
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
                        Sequence(declareVariable(), _pushVariableToArgumentList()),
                        ZeroOrMore(
                                comma(),
                                FirstOf(
                                        Sequence(declareVariable(), _pushVariableToArgumentList()),
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
                        functionArgumentWithBrackets(),
                        _pushFunctionExtensionToDeclaration()
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
                _pushAst_ExtractTopAstsAndSetAsAttributes("var", "name", "type")
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
                assignSymbol(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression assigned to variable")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("init_expression", UNKNOWN, UNKNOWN)
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
                _pushAstWithMatchedStringAsAttribute("number", "value"),
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
                _pushAstWithMatchedStringAsAttribute("string", "value"),
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
                _pushAstWithMatchedStringAsAttribute("string", "value"),
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
                        ),
                        _pushAst_ExtractTopAstAndSetAsChild("unary_operation"),
                        _setAttributeOnLastAst("operation", "not")
                ),
                Sequence(
                        equalRule(),
                        ZeroOrMore(
                                FirstOf(
                                        keyword("&&"),
                                        keyword("||")
                                ),
                                _pushAstWithMatchedStringAsAttribute("binary_operation", "operation"),
                                FirstOf(
                                        equalRule(),
                                        actionFail("after 'and'/'or' should be another expression")
                                ),
                                _pushBinaryOperation()
                        )
                )
        );
    }

    public Rule equalRule() {
        return Sequence(
                sumRule(),
                ZeroOrMore(
                        Sequence(
                                FirstOf(
                                        keyword("!="), keyword("=="), keyword("="), keyword(">="), keyword("<="), keyword(">"), keyword("<")
                                ),
                                _pushAstWithMatchedStringAsAttribute("binary_operation", "operation")
                        ),
                        FirstOf(
                                sumRule(),
                                actionFail("Expected expression after comparing operation")
                        ),
                        _pushBinaryOperation()
                )
        );
    }

    public Rule sumRule() {
        return Sequence(
                term(),
                ZeroOrMore(
                        Sequence(
                                FirstOf(
                                        keyword("+"),
                                        keyword("-")
                                ),
                                _pushAstWithMatchedStringAsAttribute("binary_operation", "operation")
                        ),
                        FirstOf(
                                term(),
                                actionFail("Expected expression after +/-")
                        ),
                        _pushBinaryOperation()
                )
        );
    }

    public Rule term() {
        return Sequence(
                atom(),
                ZeroOrMore(
                        Sequence(
                                FirstOf(keyword("/"), keyword("*")),
                                _pushAstWithMatchedStringAsAttribute("binary_operation", "operation")
                        ),
                        FirstOf(
                                atom(),
                                actionFail("Expected expression after * or /")
                        ),
                        _pushBinaryOperation()
                )
        );
    }

    public Rule atom() {
        return FirstOf(
                cast(),
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
                pointerVariable(),
                identifier()
        );
    }

    public Rule pointerVariable() {
        return Sequence(
                keyword(POINTER),
                _pushAst("pointer"),
                identifier(),
                _pushTopStackAstToNextStackAstAsChild("identifier", "pointer")
        );
    }

    public Rule structVariable() {
        return Sequence(
                _pushAst("extractField"),
                simpleVariable(),
                _pushTopStackAstToNextStackAstAsAttribute("source", UNKNOWN, "extractField"),
                OneOrMore(
                        Sequence(
                                keyword("."),
                                simpleVariable(),
                                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "extractField")
                        )
                )
        );
    }

    public Rule booleanValueRule() {
        return Sequence(
                FirstOf(
                        keyword(TRUE),
                        keyword(FALSE)
                ),
                _pushAstWithMatchedStringAsAttribute("boolean", "value")
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
                _pushAst("structure"),
                FirstOf(
                        identifier(),
                        actionFail("Expected structure name")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("name", "identifier", "structure"),
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
                Sequence(declareVariableWithSemicolon(), _pushTopStackAstToNextStackAstAsChild("var", "structure")),
                ZeroOrMore(
                        Sequence(declareVariableWithSemicolon(), _pushTopStackAstToNextStackAstAsChild("var", "structure"))
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
                _pushAstWithMatchedStringAsAttribute("identifier", "name"),
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
                _pushAst("pointer"),
                identifier(),
                keyword(POINTER),
                _pushTopStackAstToNextStackAstAsAttribute("type", "identifier", "pointer")
        );
    }

    public Rule simpleIdentifier() {
        return identifier();
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

    public Rule assignSymbol() {
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
