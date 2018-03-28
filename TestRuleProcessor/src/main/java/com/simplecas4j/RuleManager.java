package com.simplecas4j;

import com.simplecas4j.ast.Ast;
import com.simplecas4j.ast.AstHolderFactory;
import com.simplecas4j.rule.RuleType;
import com.simplecas4j.rule.RuleAndReplacementPair;
import com.simplecas4j.rule.Rule;
import com.simplecas4j.rule.RuleReplacement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class RuleManager {

    private final List<RuleAndReplacementPair> rulePairs = new ArrayList<>();

    public RuleManager() {
        initRules();
    }

    public void initRules() {

        addRule(operator("+", zm(), variable().setLabel("var"), number().setLabel("literal"), zm()),//(+ var,num) -> (+ num,var)
                replacement(swap("var", "literal")));

        addRule(operator("+", zm(), number().setLabel("n1"), number().setLabel("n2"), zm()).setLabel("outer"),//(+ num,num)->sum
                replacement(addAstReplacement("n1", "n2"), setContentFrom("n1", "outer")));
        addRule(operator("*", zm(), number().setLabel("n1"), number().setLabel("n2"), zm()).setLabel("outer"),//(* num, num)->mul
                replacement(mulAstReplacement("n1", "n2"), setContentFrom("n1", "outer")));
        addRule(operator("*", anyExpression().setLabel("singleAnyValue")).setLabel("operatorContainsSingleValue"),//(* singleOperand) ->singleOperand
                replacement(setContentFrom("singleAnyValue", "operatorContainsSingleValue")));
        addRule(operator("+", anyExpression().setLabel("singleAnyValue")).setLabel("operatorContainsSingleValue"),//(+ singleOperand) ->singleOperand
                replacement(setContentFrom("singleAnyValue", "operatorContainsSingleValue")));
        addRule(operator("+", zm(), number("0").setLabel("zeroLiteral"), anyExpression(), zm()),//(+ 0,any) -> any
                replacement(removeLabeledAst("zeroLiteral")));

        addRule(operator("*", zm(), variable().setLabel("var"), number().setLabel("literal"), zm()),//(* var, num)->(* num,var)
                replacement(swap("literal", "var")));
        addRule(operator("+", zm(), operator("+", zm()).setLabel("innerPlus"), zm()).setLabel("outerPlus"),//(+any1, (+any2,any3))=(+ any1,any2,any3)
                replacement(moveChildren("innerPlus", "outerPlus"), removeLabeledAst("innerPlus")));
        addRule(operator("+", zm(), parentheses(anyExpression()).setLabel("par"), number().setLabel("literal"), zm()),//(+ (anyOperation), num)->(+ num,(anyOperation))
                replacement(swap("par", "literal")));
        addRule(operator("+", zm(), anyExpression(), parentheses(operator("+", zm()).setLabel("inner")).setLabel("outer"), zm()),//(+ any1,((+ any2, any3))) -> (+ any1, any2, any3)
                replacement(swap("inner", "outer"), removeLabeledAst("outer")));
        addRule(operator("*", zm(), parentheses(anyExpression()).setLabel("par"), number().setLabel("literal"), zm()),//(* (any1), num) -> (* num,(any1))
                replacement(swap("par", "literal")));
        addRule(operator("*", zm(), number("0").setLabel("zero"), anyExpression().setLabel("expression"), zm()),//(* 0, any) -> 0
                replacement(removeLabeledAst("expression")));
        addRule(operator("*", number().setLabel("zero")).setLabel("operator"),//(* 0) -> 0
                replacement(swap("zero", "operator"), removeLabeledAst("operator")));
        addRule(operator("*", zm(), number("1").setLabel("number"), anyExpression().setLabel("expression"), zm()),//(* 1,any) -> (* any)
                replacement(removeLabeledAst("number")));

        addRule(parentheses(number().setLabel("inner")).setLabel("outer"), //(num)->num
                replacement(setContentFrom("inner", "outer")));
        addRule(parentheses(variable().setLabel("inner")).setLabel("outer"), //(var)->var
                replacement(setContentFrom("inner", "outer")));

        addRule(operator("/", number().setLabel("num1"), number().setLabel("num2")).setLabel("outer"),//(/ num1,num2)->dividedNumber
                replacement(divAstReplacement("num1", "num2"), setContentFrom("num1", "outer")));

        addRule(operator("/", anyExpression().setLabel("any1"), anyExpression().setLabel("any2").setSameAsLabel("any1")).setLabel("outer"),//(/ any1,any1)->1
replacement(replaceAstWithNewContent("outer", Ast.numberFactory("1"))));

        // (/ some,1) -> some
        addRule(operator("/", anyExpression().setLabel("inner"), number("1")).setLabel("outer"),
                replacement(setContentFrom("inner", "outer")));

        //(/ any1,(* any1, any2))->(/ 1 (* any2)))
        addRule(operator("/",
                        anyExpression().setLabel("any1"),
                        operator("*", zm(),
                                anyExpression().setLabel("any2").setSameAsLabel("any1"),
                                zm()
                        ).setLabel("innerMul")
                ),
                replacement(printRule("Reduction", "any1", "any2", "innerMul"), replaceAstWithNewContent("any1", Ast.numberFactory("1")), replaceAstWithNewContent("any2", Ast.numberFactory("1"))));

        //(/ (* any1, any2),any1)->(* any2)   //  x*8/x
        addRule(
                operator("/",
                        operator("*", zm(),
                                anyExpression().setLabel("any1"),
                                zm()
                        ).setLabel("innerMul"),
                        anyExpression().setLabel("any2").setSameAsLabel("any1")
                ).setLabel("outerDiv"),
                replacement(setContentFrom("innerMul", "outerDiv"), removeLabeledAst("any1")));

    }

    private RuleReplacement replaceAstWithNewContent(String label, AstHolderFactory newContent) {
        return (((matchContext) -> {
            Ast oldAst = matchContext.getAstHolder(label);
            oldAst.setContentFromXAstAndRemoveX(newContent.construct());
        }));
    }

    private RuleReplacement moveChildren(String labelFrom, String labelTo) {
        return ((matchContext) -> {
            Ast from = matchContext.getAstHolder(labelFrom);
            Ast to = matchContext.getAstHolder(labelTo);
            List<Ast> children = from.getChildren();
            from.setChildren(null);
            to.getChildren().addAll(children);
            for (Ast child : children) {
                child.setParent(to);
            }
            to.setDirtyRecursivelyToParents(true);
            from.setDirty(true);
        });
    }

    private RuleReplacement printRule(String message, String... labels) {
        return (matchContext) -> {
            System.out.println(message);
            if (labels != null) {
                for (String label : labels) {
                    System.out.print(label);
                    System.out.print(":");
                    System.out.println(matchContext.getAstHolder(label).deepToString());
                }
            }
        };
    }

    private RuleReplacement addAstReplacement(String arg1, String arg2) {
        return (matchContext) -> {
            Ast a1 = matchContext.getAstHolder(arg1);
            Ast a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValueUpdateDirtyStatus(String.valueOf(i1 + i2));
            a2.removeThisFromParent();
        };
    }

    private RuleReplacement divAstReplacement(String arg1, String arg2) {
        return (matchContext) -> {
            Ast a1 = matchContext.getAstHolder(arg1);
            Ast a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValueUpdateDirtyStatus(String.valueOf(i1 / i2));
            a2.removeThisFromParent();
        };
    }

    private RuleReplacement mulAstReplacement(String arg1, String arg2) {
        return (matchContext) -> {
            Ast a1 = matchContext.getAstHolder(arg1);
            Ast a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValueUpdateDirtyStatus(String.valueOf(i1 * i2));
            a2.removeThisFromParent();
        };
    }

    private RuleReplacement removeLabeledAst(String label) {
        return (matchContext) -> {
            Ast ast = matchContext.getAstHolder(label);
            if (ast == null) {
                throw new IllegalArgumentException("Cannot find label [" + label + "]");
            }

            ast.removeThisFromParent();
        };
    }

    private RuleReplacement swap(String label1, String label2) {
        return (matchContext) -> {
            Ast ast1 = matchContext.getAstHolder(label1);
            Ast ast2 = matchContext.getAstHolder(label2);
            matchContext.addMatchedAst(label1, ast2);
            matchContext.addMatchedAst(label2, ast1);

            ast1.swapContent(ast2);
        };
    }

    private RuleReplacement setContentFrom(String from, String to) {
        return (matchContext) -> {
            Ast fromAst = matchContext.getAstHolder(from);
            Ast toAst = matchContext.getAstHolder(to);
            matchContext.getMatchedAsts().remove(from);
            toAst.setContentFromXAstAndRemoveX(fromAst);
        };
    }

    public Rule number(String value) {
        return new Rule().setValue(value).setRuleType(RuleType.NUMBER).addAttributes("literal", "number");
    }

    public Rule number() {
        return new Rule().setValue(EquationExecutor.ANY_VALUE).setRuleType(RuleType.NUMBER).addAttributes("literal", "number");
    }

    public Rule anyExpression() {
        return new Rule().setRuleType(RuleType.ANY);
    }

    /**
    Zero Or More Any expressions
     */
    public Rule zm() {
        return new Rule().setRuleType(RuleType.ZERO_OR_MORE);
    }

    public Rule variable() {
        return new Rule().setValue(EquationExecutor.ANY_VALUE).setRuleType(RuleType.VAR);
    }

    public Rule parentheses(Rule child) {
        return new Rule().setRuleType(RuleType.PARENTHESES).children(child);
    }

    public Rule operator(String operator, Rule... children) {
        return new Rule().setValue(operator).setRuleType(RuleType.OPERATOR).children(children);
    }

    public void addRule(Rule rule, RuleReplacement[] ruleReplacement) {
        RuleAndReplacementPair rrp = new RuleAndReplacementPair(rule, ruleReplacement);
        rulePairs.add(rrp);
    }

    public RuleReplacement[] replacement(RuleReplacement... replacementRules) {
        return replacementRules;
    }

    public List<RuleAndReplacementPair> getRulePairs() {
        return rulePairs;
    }
}
