package com.kubadziworski.domain.node;

import org.antlr.v4.runtime.ParserRuleContext;


public class RuleContextElementImpl implements NodeData {

    private final ParserRuleContext ruleContext;
    private final boolean shouldAnalyze;

    public RuleContextElementImpl() {
        this.ruleContext = new ParserRuleContext();
        shouldAnalyze = false;
    }

    public RuleContextElementImpl(ParserRuleContext ruleContext) {
        this.ruleContext = ruleContext;
        shouldAnalyze = true;
    }

    @Override
    public boolean shouldAnalyze() {
        return shouldAnalyze;
    }

    @Override
    public int getStartLine() {
        return ruleContext.getStart().getLine();
    }

    @Override
    public String getText() {
        return ruleContext.getText();
    }

    @Override
    public int getEndLine() {
        return ruleContext.getStop().getLine();
    }
}
