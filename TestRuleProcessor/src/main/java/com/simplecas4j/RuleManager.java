package com.simplecas4j;

import com.simplecas4j.ast.AstHolder;
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
        addRule(operator("+", zm(), number().setLabel("n1"), number().setLabel("n2"), zm()).setLabel("outer"),//(+ num,num)->sum
                replacement(addAst("n1", "n2"),swap("n1", "outer",true), removeLabeledAst("outer")));
        addRule(operator("*", zm(), number().setLabel("n1"), number().setLabel("n2"), zm()).setLabel("outer"),//(* num, num)->mul
                replacement(mulAst("n1", "n2"),swap("n1", "outer",true), removeLabeledAst("outer")));
        addRule(operator("*", anyExpression().setLabel("singleAnyValue")).setLabel("operatorContainsSingleValue"),//(* singleOperand) ->singleOperand
                replacement(swap("singleAnyValue", "operatorContainsSingleValue"), removeLabeledAst("operatorContainsSingleValue")));
        addRule(operator("+", anyExpression().setLabel("singleAnyValue")).setLabel("operatorContainsSingleValue"),//(+ singleOperand) ->singleOperand
                replacement(swap("singleAnyValue", "operatorContainsSingleValue"), removeLabeledAst("operatorContainsSingleValue")));
        addRule(operator("+", zm(), number("0").setLabel("zeroLiteral"), anyExpression(), zm()),//(+ 0,any) -> any
                replacement(removeLabeledAst("zeroLiteral")));
        addRule(operator("+", zm(), variable().setLabel("var"), number().setLabel("literal"), zm()),//(+ var,num) -> (+ num,var)
                replacement(swap("var", "literal")));
     //   addRule(operator("+", zm(), variable().setLabel("var"), number().setLabel("literal"), zm()),//
     //           replacement(swap("var", "literal")));
        addRule(operator("+", zm(), operator("+", zm()).setLabel("innerPlus"), zm()).setLabel("outerPlus"),//(+any1, (+any2,any3))=(+ any1,any2,any3)
                replacement(moveChildren("innerPlus", "outerPlus"), removeLabeledAst("innerPlus")));
        addRule(operator("+", zm(), parentheses(anyExpression()).setLabel("par"), number().setLabel("literal"), zm()),//(+ (anyOperation), num)->(+ num,(anyOperation))
                replacement(swap("par", "literal", false)));
        addRule(operator("+", zm(), anyExpression(), parentheses(operator("+", zm()).setLabel("inner")).setLabel("outer"), zm()),//(+ any1,((+ any2, any3))) -> (+ any1, any2, any3)
                replacement(swap("inner", "outer", false), removeLabeledAst("outer")));
        addRule(operator("*", zm(), parentheses(anyExpression()).setLabel("par"), number().setLabel("literal"), zm()),//(* (any1), num) -> (* num,(any1))
                replacement(swap("par", "literal", false)));
        addRule(operator("*", zm(), number("0").setLabel("zero"), anyExpression().setLabel("expression"), zm()),//(* 0, any) -> 0
                replacement(removeLabeledAst("expression"), removeLabeledAst("zero")));
        addRule(operator("*", zm(), number("1").setLabel("number"), anyExpression().setLabel("expression"), zm()),//(* 1,any) -> (* any)
                replacement(removeLabeledAst("number")));
        addRule(operator("*", zm(), variable().setLabel("var"), number().setLabel("literal"), zm()),//(* var, num)->(* num,var)
                replacement(swap("literal", "var")));
        addRule(parentheses(number().setLabel("inner")).setLabel("outer"), //(num)->num
                replacement(swap("inner", "outer"), removeLabeledAst("outer")));
        addRule(parentheses(variable().setLabel("inner")).setLabel("outer"), replacement(//(var)->var
                swap("inner", "outer"),removeLabeledAst("outer")));
        
        addRule(operator("/", number().setLabel("num1"),number().setLabel("num2")).setLabel("outer"),//(/ num1,num2)->dividedNumber
                 replacement(divAst("num1", "num2"),swap("num1", "outer",true), removeLabeledAst("outer")));
    }

    public RuleReplacement moveChildren(String labelFrom, String labelTo) {
        return ((matchContext) -> {
            AstHolder from = matchContext.getAstHolder(labelFrom);
            AstHolder to = matchContext.getAstHolder(labelTo);
            List<AstHolder> children = from.getChildren();
            from.setChildren(null);
            to.getChildren().addAll(children);
            for (AstHolder child : children) {
                child.setParent(to);
            }
            to.setDirtyRecursivelyToParents(true);
            from.setDirty(true);
        });
    }

    public RuleReplacement printRule(String message, String... labels) {
        return (matchContext) -> {
            System.out.println(message);
            if (labels != null) {
                for (String label : labels) {
                    System.out.print(label);
                    System.out.print(":");
                    System.out.println(matchContext.getAstHolder(label));
                }
            }
        };
    }

    public RuleReplacement addAst(String arg1, String arg2) {
        return (matchContext) -> {
            AstHolder a1 = matchContext.getAstHolder(arg1);
            AstHolder a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValue(String.valueOf(i1 + i2));
            a2.removeThisFromParent();
        };
    }
    
    public RuleReplacement divAst(String arg1, String arg2) {
        return (matchContext) -> {
            AstHolder a1 = matchContext.getAstHolder(arg1);
            AstHolder a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValue(String.valueOf(i1 / i2));
            a2.removeThisFromParent();
        };
    }

    public RuleReplacement mulAst(String arg1, String arg2) {
        return (matchContext) -> {
            AstHolder a1 = matchContext.getAstHolder(arg1);
            AstHolder a2 = matchContext.getAstHolder(arg2);
            Integer i1 = Integer.parseInt(a1.getValue());
            Integer i2 = Integer.parseInt(a2.getValue());
            a1.setValue(String.valueOf(i1 * i2));
            a2.removeThisFromParent();
        };
    }

    public RuleReplacement removeLabeledAst(String label) {
        return (matchContext) -> {
            AstHolder ast=matchContext.getAstHolder(label);
            if(ast==null){
                throw new IllegalArgumentException("Cannot find label ["+label+"]");
            }
            ast.removeThisFromParent();
        };
    }

    public RuleReplacement swap(String label1, String label2, boolean swapChildren) {
        return (matchContext) -> {
            AstHolder ast1 = matchContext.getAstHolder(label1);
            AstHolder ast2 = matchContext.getAstHolder(label2);
            matchContext.addMatchedAst(label1, ast2);
            matchContext.addMatchedAst(label2, ast1);
            ast1.swapContentWithAnotherAstHolder(ast2, swapChildren);
        };
    }

    public RuleReplacement swap(String label1, String label2) {
        return (matchContext) -> {
            AstHolder ast1 = matchContext.getAstHolder(label1);
            AstHolder ast2 = matchContext.getAstHolder(label2);
            ast1.swapContentWithAnotherAstHolder(ast2, true);
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
