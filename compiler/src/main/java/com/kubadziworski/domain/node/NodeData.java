package com.kubadziworski.domain.node;

/**
 * Created by plozano on 11/17/2016.
 */
public interface NodeData {

    boolean shouldAnalyze();

    int getStartLine();

    String getText();

    int getEndLine();
}
