package com.kubadziworski.domain.node;


public interface RdElement {

    boolean shouldAnalyze();

    int getStartLine();

    String getText();

    int getEndLine();
}
