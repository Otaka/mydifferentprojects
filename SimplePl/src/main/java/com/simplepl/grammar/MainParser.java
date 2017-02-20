package com.simplepl.grammar;

import com.simplepl.grammar.matchers.JavaUnicodeMatcherStartString;
import com.simplepl.grammar.matchers.JavaUnicodeMatcherString;
import org.parboiled.Rule;

/**
 * @author Dmitry
 */
public class MainParser extends MainParserActions {

    public Rule main(){
        return ZeroOrMore(line());
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
                expressionsEndsWithSemicolon(),
                closeCurleyBracket()
        );
    }

    public Rule expressionsEndsWithSemicolon() {
        return ZeroOrMore(
                Sequence(
                        expression(),
                        FirstOf(
                                semicolon(),
                                actionFail("Expected ';' after the expression")
                        )
                )
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
                functionRule(),
                structure(),
                declareArray(),
                declareVariableAndAssign(),
                declareVariable(),
                checkExpression()
        // actionFail("Cannot parse the expression")
        );
    }

    public Rule testFunctionCall() {
        return Sequence(functionCall(), EOI);
    }

    public Rule functionCall() {
        return Sequence(
                identifier(),
                openBracket(),
                expressionsSeparatedWithComma(),
                closeBracket()
        );
    }

    public Rule functionDeclaration() {
        return Sequence(
                keyword(FUN),
                FirstOf(
                        identifier(),
                        actionFail("Expected function name")
                ),
                FirstOf(
                        openBracket(),
                        actionFail("Expected open bracket")
                ),
                argumentList(),
                FirstOf(
                        closeBracket(),
                        actionFail("Expected close bracket")
                )
        );
    }

    public Rule testDeclareVariable() {
        return Sequence(declareVariable(), EOI);
    }

    public Rule declareVariable() {
        return Sequence(
                typeIdentifier(),
                identifier()
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
                        OneOrMore(Digit()),
                        Optional('.', OneOrMore(Digit()))
                ),
                possibleSpace()
        ).suppressSubnodes();
    }

    public Rule Digit() {
        return Sequence(possibleSpace(), OneOrMore(CharRange('0', '9')), possibleSpace());
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
                                        actionFail("Found end of line while reading string")),
                                Escape(),
                                Sequence(TestNot("\""), ANY)
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
                                pushHelperString("and/or_operation"),
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
                        FirstOf(keyword("!="), keyword("="), keyword(">="), keyword("<="), keyword(">"), keyword("<")),
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
                        FirstOf(keyword("*"), keyword("/")),
                        pushHelperString("* or / operation"),
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
                genericStringRule(),
                rawStringRule(),
                parens()
        );
    }

    public Rule variable() {
        return identifier();
    }

    public Rule booleanValueRule() {
        return FirstOf(
                keyword("true"),
                keyword("false")
        );
    }

    public Rule parens() {
        return Sequence(
                openBracket(),
                checkExpression(),
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
                keyword("structure"),
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
        return OneOrMore(oneSpaceCharacter());
    }

    public Rule possibleSpace() {
        return ZeroOrMore(oneSpaceCharacter());
    }

    public Rule identifier() {
        return Sequence(
                possibleSpace(),
                new JavaUnicodeMatcherStartString(),
                ZeroOrMore(
                        new JavaUnicodeMatcherString()),
                possibleSpace()
        ).suppressSubnodes();
    }

    public Rule typeIdentifier() {
        return identifier();
    }

    public Rule keyword(String val) {
        return Sequence(possibleSpace(), val, possibleSpace()).suppressSubnodes();
    }

    public Rule openBracket() {
        return keyword("(");
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

    public Rule comma() {
        return keyword(",");
    }

    public Rule STR_TERMINAL(char... character) {
        return Sequence(ZeroOrMore(NoneOf(character)), character);
    }

    public String FUN = "fun";

}
