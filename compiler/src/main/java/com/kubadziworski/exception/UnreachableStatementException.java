package com.kubadziworski.exception;


import org.antlr.v4.runtime.ParserRuleContext;

public class UnreachableStatementException extends RuntimeException {

    public UnreachableStatementException(ParserRuleContext ruleContext) {
        super("Unreachable statement at line " + ruleContext.start.getLine() + ": \" \n Content:  " + ruleContext.getText());
    }
}
