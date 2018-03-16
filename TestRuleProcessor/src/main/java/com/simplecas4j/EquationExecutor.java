package com.simplecas4j;

import com.simplecas4j.rule.RuleType;
import com.simplecas4j.rule.MatchContext;
import com.simplecas4j.rule.RuleAndReplacementPair;
import com.simplecas4j.ast.Ast;
import com.simplecas4j.ast.AstHolder;
import com.simplecas4j.rule.Rule;
import com.simplecas4j.rule.RuleReplacement;
import com.simplecas4j.rule.SimplificationResult;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class EquationExecutor {

    public static final String ANY_VALUE = "any";
    private RuleManager ruleManager;

    public EquationExecutor() {
        ruleManager = new RuleManager();
    }

    public EquationExecutor(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public double evaluateAst(AstHolder ast) {
        simplifyAst(ast);
        return 0;
    }

    private SimplificationResult simplifyAst(AstHolder ast) {
        boolean someRuleExecuted = false;
        while (true) {
            SimplificationResult result = simplifyAstAndItChildsRound(ast,0);
            if (result == SimplificationResult.RULES_EXECUTED) {
                someRuleExecuted = true;
            } else {
                break;
            }
        }
        if (someRuleExecuted) {
            return SimplificationResult.RULES_EXECUTED;
        } else {
            return SimplificationResult.NO_RULES_EXECUTED;
        }
    }

    private SimplificationResult simplifyAstAndItChildsRound(AstHolder ast, int level) {
        if (ast.getType() == RuleType.NUMBER) {
            return SimplificationResult.NO_RULES_EXECUTED;
        }
        if (ast.getType() == RuleType.VAR) {
            return SimplificationResult.NO_RULES_EXECUTED;
        }

        boolean someRuleExecuted = false;
        while (true) {
            System.out.print(createString(' ', level));
            System.out.println("Apply to " + ast.deepToString());
            if (tryToFindAndApplyOneMatchedRuleToAstHolder(ast, ast) == SimplificationResult.NO_RULES_EXECUTED) {
                break;
            } else {
                someRuleExecuted = true;
            }
        }
        List<AstHolder> children = ast.getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                AstHolder child = children.get(i);
                SimplificationResult result = simplifyAstAndItChildsRound(child,level+1);
                if (result == SimplificationResult.RULES_EXECUTED) {
                    someRuleExecuted = true;
                }
            }
        }

        if (someRuleExecuted) {
            return SimplificationResult.RULES_EXECUTED;
        } else {
            return SimplificationResult.NO_RULES_EXECUTED;
        }
    }
    
    private String createString(char c, int count){
        if(count==0){
            return "";
        }
        StringBuilder sb=new StringBuilder(count);
        for(int i=0;i<count;i++){
            sb.append(c);
        }
        return sb.toString();
    }

    private SimplificationResult tryToFindAndApplyOneMatchedRuleToAstHolder(AstHolder ast, AstHolder astForProcessing) {
        if (astForProcessing.isDirty() == false) {
            return SimplificationResult.NO_RULES_EXECUTED;
        }

        for (RuleAndReplacementPair rrp : ruleManager.getRulePairs()) {
            MatchContext matchContext = new MatchContext();
            if (tryToMatchRule(astForProcessing, rrp.getRule(), matchContext) == MatchResult.MATCH_OK) {
                applyReplacement(rrp, matchContext);
                astForProcessing.setDirtyRecursivelyToParents(true);
                return SimplificationResult.RULES_EXECUTED;
            }
        }

        astForProcessing.setDirty(false);
        return SimplificationResult.NO_RULES_EXECUTED;
    }

    private void applyReplacement(RuleAndReplacementPair rrp, MatchContext matchContext) {
        for (RuleReplacement replacement : rrp.getRuleReplacement()) {
            replacement.runReplacement(matchContext);
        }
    }

    private void addLabelToMatchContextIfNeeded(AstHolder ast, Rule rule, MatchContext matchContext) {
        if (rule.getLabel() != null) {
            matchContext.addMatchedAst(rule.getLabel(), ast);
        }
    }

    private MatchResult tryToMatchRule(AstHolder ast, Rule rule, MatchContext matchContext) {
        if (rule.getType() == RuleType.ANY) {
            addLabelToMatchContextIfNeeded(ast, rule, matchContext);
            return MatchResult.MATCH_OK;
        }

        if (!rule.getType().equals(ast.getType())) {
            return MatchResult.NO_MATCH;
        }

        switch (rule.getType()) {
            case ANY:
                addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                return MatchResult.MATCH_OK;
            case OPERATOR:
                if (ast.getType() != RuleType.OPERATOR) {
                    return MatchResult.NO_MATCH;
                }

                if (!rule.getValue().equals(ANY_VALUE)) {
                    if (!rule.getValue().equals(ast.getValue())) {
                        return MatchResult.NO_MATCH;
                    }
                }

                if (rule.getChildren() != null && !rule.getChildren().isEmpty()) {
                    List<Rule> childrenRules = rule.getChildren();
                    List<AstHolder> astChildren = ast.getChildren();
                    if (astChildren == null || astChildren.isEmpty()) {
                        return MatchResult.NO_MATCH;
                    }
                    MatchResult matchResult = tryToMatchChildrenRules(childrenRules, ast.getChildren(), 0, 0, matchContext);
                    if (matchResult == MatchResult.MATCH_OK) {
                        addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                    }

                    return matchResult;
                }
                throw new IllegalStateException("Not implemented yet");
            case NUMBER:
                if (ast.getType() == RuleType.NUMBER) {
                    if (rule.getValue().equals(ANY_VALUE)) {
                        addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                        return MatchResult.MATCH_OK;
                    } else {
                        if (rule.getValue().equals(ast.getValue())) {
                            addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                            return MatchResult.MATCH_OK;
                        } else {
                            return MatchResult.NO_MATCH;
                        }
                    }
                }
                break;
            case PARENTHESES:
                if (ast.getType() != RuleType.PARENTHESES) {
                    return MatchResult.NO_MATCH;
                }

                if (ast.getChildren().isEmpty()) {
                    if (!checkNoOtherRulesExceptZeroOrMore(rule.getChildren(), 0, matchContext)) {
                        return MatchResult.NO_MATCH;
                    }
                }

                MatchResult matchResult = tryToMatchChildrenRules(rule.getChildren(), ast.getChildren(), 0, 0, matchContext);
                if (matchResult == MatchResult.MATCH_OK) {
                    addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                }

                return matchResult;
            case VAR:
                if (ast.getType() != RuleType.VAR) {
                    return MatchResult.NO_MATCH;
                }

                if (rule.getValue().equals(ANY_VALUE)) {
                    addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                    return MatchResult.MATCH_OK;
                }
                if (!rule.getValue().equals(ast.getValue())) {
                    return MatchResult.NO_MATCH;
                }

                addLabelToMatchContextIfNeeded(ast, rule, matchContext);
                return MatchResult.MATCH_OK;
            default:
                break;
        }

        throw new IllegalStateException("Not implemented yet... we should not be there.... Rule type [" + rule.getType() + "]");
    }

    private MatchResult tryToMatchChildrenRules(List<Rule> childrenRules, List<AstHolder> astHolders, int ruleIndex, int astChildIndex, MatchContext matchContext) {
        Rule rule = childrenRules.get(ruleIndex);
        if (rule.getType() == RuleType.ZERO_OR_MORE) {
            if (ruleIndex == childrenRules.size() - 1) {
                return MatchResult.MATCH_OK;//zero or more is the last rule. Match always
            }
            for (int i = 0; i < astHolders.size(); i++) {
                if ((ruleIndex + 1) >= childrenRules.size()) {
                    return MatchResult.MATCH_OK;//zero or more is the last rule, that is why it should be matched
                }
                MatchResult childMatch = tryToMatchChildrenRules(childrenRules, astHolders, ruleIndex + 1, astChildIndex + i, matchContext);
                if (childMatch == MatchResult.NO_MORE_ELEMENT) {
                    return MatchResult.NO_MORE_ELEMENT;
                }
                if (childMatch == MatchResult.MATCH_OK) {
                    return MatchResult.MATCH_OK;
                }
                //no match. Iterate next
            }
            return MatchResult.NO_MATCH;
        } else {
            MatchResult matchResult = tryToMatchRule(astHolders.get(astChildIndex), childrenRules.get(ruleIndex), matchContext);
            if (matchResult == MatchResult.MATCH_OK) {
                if (ruleIndex == (childrenRules.size() - 1)) {
                    if (astChildIndex == (astHolders.size() - 1)) {
                        return MatchResult.MATCH_OK;
                    } else {
                        if (checkThereIsZeroOrMore(childrenRules)) {
                            return MatchResult.MATCH_OK;
                        } else {
                            return MatchResult.NO_MATCH;
                        }
                    }
                }
                if (astChildIndex == (astHolders.size() - 1)) {
                    if (checkNoOtherRulesExceptZeroOrMore(childrenRules, ruleIndex + 1, matchContext)) {
                        return MatchResult.MATCH_OK;
                    } else {
                        return MatchResult.NO_MATCH;//rules are not finished yet(no enough data), but ast has been finished, it is no_match
                    }
                }
                MatchResult otherResult = tryToMatchChildrenRules(childrenRules, astHolders, ruleIndex + 1, astChildIndex + 1, matchContext);
                return otherResult;
            }

            return matchResult;
        }
    }

    private boolean checkThereIsZeroOrMore(List<Rule> rules) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).getType() == RuleType.ZERO_OR_MORE) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNoOtherRulesExceptZeroOrMore(List<Rule> rules, int ruleIndex, MatchContext matchContext) {
        for (int i = ruleIndex; i < rules.size(); i++) {
            Rule r = rules.get(i);
            if (r.getType() != RuleType.ZERO_OR_MORE) {
                return false;
            }
        }

        return true;
    }

    public AstHolder var(String name) {
        return new AstHolder(new Ast().setType(RuleType.VAR).setValue(name));
    }

    public AstHolder op(String type, AstHolder... children) {
        return new AstHolder(new Ast().setType(RuleType.OPERATOR).setValue(type)).setChildren(asList(children));
    }

    public AstHolder number(String number) {
        return new AstHolder(new Ast().setType(RuleType.NUMBER).setValue(number));
    }

    public AstHolder parentheses(AstHolder child) {
        return new AstHolder(new Ast().setType(RuleType.PARENTHESES)).setChildren(asList(child));
    }

    private <T> List<T> asList(T... objs) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < objs.length; i++) {
            result.add(objs[i]);
        }
        return result;
    }

    private String calculateMd5(String string) {
        byte[] bytesOfMessage = string.getBytes(StandardCharsets.UTF_8);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("For some reason your java machine does not support MD5 algorithm");
        }

        byte[] thedigest = md.digest(bytesOfMessage);
        StringBuilder sb = new StringBuilder(thedigest.length * 2);
        for (int i = 0; i < thedigest.length; i++) {
            sb.append(Integer.toHexString(thedigest[i] & 0xFF));
        }
        return sb.toString();
    }

    private enum MatchResult {
        NO_MATCH, NO_MORE_ELEMENT, MATCH_OK
    }
}
