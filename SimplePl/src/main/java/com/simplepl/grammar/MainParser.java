package com.simplepl.grammar;

import com.simplepl.grammar.ast.Ast;
import com.simplepl.grammar.matchers.JavaUnicodeMatcherStartString;
import com.simplepl.grammar.matchers.JavaUnicodeMatcherString;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.Rule;

/**
 * @author Dmitry
 */
public class MainParser extends MainParserActions {

    public Rule main() {
        return Sequence(
                _pushAst("module"),
                ZeroOrMore(
                        line(),
                        _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "module")
                ),
                EOI);
    }

    public Rule line() {
        return expression();
    }

    public Rule functionRule() {
        return Sequence(
                declareFunction(),
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

    public Rule arrayGet() {
        return Sequence(
                _pushAst("arrayGet"),
                OneOrMore(
                        openSquareBracket(),
                        FirstOf(
                                expression(),
                                actionFail("Expected expression as array index inside brackets")
                        ),
                        FirstOf(
                                closeSquareBracket(),
                                actionFail("You forget to close the array ']'")
                        ),
                        _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "arrayGet")
                ),
                _pushUnderTopStackAstToTopStackAstAsAttribute("source", UNKNOWN, "arrayGet")
        );
    }

    public Rule testExpression() {
        return Sequence(expression(), EOI);
    }

    public Rule expression() {
        return FirstOf(
                breakStatement(),
                continueStatement(),
                forStatement(), continueStatement(),
                whileStatement(),
                ifStatement(),
                newStatement(),
                deleteStatement(),
                importStatement(),
                functionRule(),
                structureDeclaration(),
                defineNewType(),
                declareArray(),
                declareVariableAndAssign(),
                declareVariable(),
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

    public Rule importStatement() {
        return Sequence(
                keyword(IMPORT),
                _pushAst("import"),
                Optional(
                        Sequence(
                                keyword("static"),
                                _pushAttributeOnLastAst("static", "true")
                        )
                ),
                FirstOf(
                        singleIdentifier(),
                        actionFail("Expected package")
                ),
                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "import"),
                ZeroOrMore(
                        dot(),
                        FirstOf(
                                Sequence(
                                        singleIdentifier(),
                                        _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "import")
                                ),
                                actionFail("Expected package")
                        )
                )
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

    public Rule functionCallWithoutIdentifier() {
        return Sequence(
                _pushAst("function_call"),
                openBracket(),
                _pushUnderTopStackAstToTopStackAstAsAttribute("name", UNKNOWN, "function_call"),
                expressionsSeparatedWithComma(),
                _pushTopStackAstToNextStackAstAsChild(UNKNOWN, "function_call"),////?????? first UNKNOWN should be replaced to "expressions_list"
                closeBracket(),
                Optional(
                        Sequence(extensionExpressionBlock(), _pushTopStackAstToNextStackAstAsChild("function_extension", "function_call"))
                )
        );
    }

    public Rule functionCall() {
        return Sequence(
                _pushAst("function_call"),
                singleIdentifier(),
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
                Sequence(singleIdentifier(), _pushTopStackAstToNextStackAstAsChild("identifier", "extension_arg_rename")),
                ZeroOrMore(
                        comma(),
                        FirstOf(
                                Sequence(singleIdentifier(), _pushTopStackAstToNextStackAstAsChild("identifier", "extension_arg_rename")),
                                actionFail("Expected identifier for the renamed argument")
                        )
                )
        );
    }

    public Rule processFunctionReturnValue() {
        return Sequence(
                FirstOf(
                        typeIdentifier(),
                        actionFail("Expected return type")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("returnValue", UNKNOWN, "function"));
    }

    public Rule declareFunction() {
        return Sequence(
                keyword(FUN),
                _pushAst("function"),
                functionAnnotations(),
                processFunctionReturnValue(),
                FirstOf(
                        singleIdentifier(),
                        actionFail("Expected function name")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("name", "identifier", "function"),
                functionArgumentWithBrackets(),
                _pushTopStackAstToNextStackAstAsAttribute("arguments", "function_arguments", "function"),
                declareFunctionExtension()
        );
    }

    public Rule functionAnnotations() {
        return Optional(
                _pushAst("functionAnnotations"),
                ZeroOrMore(
                        keyword("@"),
                        singleIdentifier(),
                        _pushTopStackAstToNextStackAstAsChild("identifier", "functionAnnotations")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("annotations", "functionAnnotations", "function")
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

    public Rule declareFunctionExtension() {
        return Optional(
                Sequence(
                        keyword(EXTENSION),
                        FirstOf(
                                singleIdentifier(),
                                actionFail("Expected extension return value")
                        ),
                        FirstOf(
                                functionArgumentWithBrackets(),
                                actionFail("Expected extension argument list")
                        ),
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
                singleIdentifier(),
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
                keyword("array"),
                typeIdentifier(),
                arrayDeclarerSquares(),
                FirstOf(
                        singleIdentifier(),
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
                //pushVariableAssignAction()
            _pushTopStackAstToNextStackAstAsAttribute("init_expression", UNKNOWN, UNKNOWN)
        );
    }
/*
    public Action pushVariableAssignAction() {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Ast initExpression=(Ast) context.getValueStack().pop();
                Ast declaredVariableAst=(Ast) context.getValueStack().pop();
                String varName= declaredVariableAst.getAttributeAst("name").getAttributeString("name");
                context.getValueStack().push(declaredVariableAst);
                
                return true;
            }
        };
    }*/

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

    public Rule notRule() {
        return Sequence(
                keyword("!"),
                FirstOf(
                        checkExpression(),
                        actionFail("Expected expression after 'not'")
                ),
                _pushAst_ExtractTopAstAndSetAsChild("unary_operation"),
                _pushAttributeOnLastAst("operation", "not")
        );
    }

    public Rule checkExpression() {
        return //FirstOf(
                // notRule(),
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
                //)
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
                innerAtom(),
                ZeroOrMore(
                        Sequence(
                                FirstOf(keyword("/"), keyword("*")),
                                _pushAstWithMatchedStringAsAttribute("binary_operation", "operation")
                        ),
                        FirstOf(
                                innerAtom(),
                                actionFail("Expected expression after * or /")
                        ),
                        _pushBinaryOperation()
                )
        );
    }

    public Rule dereferencePointer() {
        return Sequence(
                POINTER,
                _pushAst("dereference"),
                _pushUnderTopStackAstToTopStackAstAsChild(UNKNOWN, "dereference")
        );
    }

    public Rule innerAtom() {
        return Sequence(
                atom(),
                Optional(
                        FirstOf(
                                arrayGet(),
                                dereferencePointer()
                        )
                ),
                ZeroOrMore(
                        structVariable(),
                        Optional(
                                FirstOf(
                                        dereferencePointer(),
                                        arrayGet(),
                                        functionCallWithoutIdentifier()
                                )
                        )
                ));
    }

    public Rule atom() {
        return FirstOf(
                notRule(),
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
        return simpleVariable();
    }

    public Rule simpleVariable() {
        return //FirstOf(
                // pointerVariable(),
                singleIdentifier();
        //);
    }

    public Rule pointerVariable() {
        return Sequence(
                keyword(POINTER),
                _pushAst("pointer"),
                singleIdentifier(),
                _pushTopStackAstToNextStackAstAsChild("identifier", "pointer")
        );
    }

    public Rule structVariable() {
        return Sequence(
                dot(),
                _pushAst("extractField"),
                _pushUnderTopStackAstToTopStackAstAsAttribute("fromWhere", UNKNOWN, "extractField"),
                singleIdentifier(),
                _pushTopStackAstToNextStackAstAsAttribute("expression", UNKNOWN, "extractField")
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
                        expression(),
                        actionFail("Expected expression inside the (...)")
                ),
                FirstOf(
                        closeBracket(),
                        actionFail("Expected closing bracket")
                )
        );
    }

    public Rule testStructure() {
        return Sequence(structureDeclaration(), EOI);
    }

    public Rule defineNewType() {
        return Sequence(
                keyword("deftype"),
                FirstOf(
                        singleIdentifier(),
                        actionFail("Expected new type name after the defining new type")
                ),
                FirstOf(
                        typeIdentifier(),
                        actionFail("Expected parent type for the defining new type")
                ),
                _pushAst("defineType"),
                _pushUnderTopStackAstToTopStackAstAsAttribute("source", UNKNOWN, "defineType"),
                _pushUnderTopStackAstToTopStackAstAsAttribute("newType", "identifier", "defineType")
        );
    }

    public Rule structureDeclaration() {
        return Sequence(
                keyword(STRUCTURE),
                _pushAst("structure"),
                FirstOf(
                        singleIdentifier(),
                        actionFail("Expected structure name")
                ),
                _pushTopStackAstToNextStackAstAsAttribute("name", "identifier", "structure"),
                FirstOf(
                        openCurleyBracket(),
                        actionFail("Expected '{' after structure name")
                ),
                FirstOf(
                        elementsOfStructureInDeclaration(),
                        actionFail("Expected structure elements inside structure body")
                ),
                FirstOf(
                        closeCurleyBracket(),
                        actionFail("Expected '}' at the end of the structure")
                )
        );
    }

    public Rule elementsOfStructureInDeclaration() {
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

    public Rule possibleSpace() {
        return ZeroOrMore(
                oneSpaceCharacter()
        );
    }

    public Rule singleIdentifier() {
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

    /*public Rule identifierWithPossiblePackage() {
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
    }*/
    public Rule typeIdentifier() {
        return FirstOf(
                pointerIdentifier(),
                typeWithPackageIdentifier(),
                simpleIdentifier()
        );
    }

    public Rule typeWithPackageIdentifier() {
        return Sequence(
                _pushAst("identifierWithPackage"),
                simpleIdentifier(),
                _pushTopStackAstToNextStackAstAsChild("identifier", "identifierWithPackage"),
                OneOrMore(
                        dot(),
                        simpleIdentifier(),
                        _pushTopStackAstToNextStackAstAsChild("identifier", "identifierWithPackage")
                )
        );
    }

    public Rule pointerIdentifier() {
        return Sequence(_pushAst("pointer"),
                singleIdentifier(),
                keyword(POINTER),
                _pushTopStackAstToNextStackAstAsAttribute("type", "identifier", "pointer")
        );
    }

    public Rule simpleIdentifier() {
        return singleIdentifier();
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

    public Rule dot() {
        return keyword(".");
    }

    /* public Rule colon() {
        return keyword(":");
    }*/
    public Rule comma() {
        return keyword(",");
    }

    /*public Rule STR_TERMINAL(char... character) {
        return Sequence(
                ZeroOrMore(
                        NoneOf(character)
                ),
                character
        );
    }*/
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
    public String IMPORT = "import";

}
