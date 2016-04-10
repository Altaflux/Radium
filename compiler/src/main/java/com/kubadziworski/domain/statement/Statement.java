package com.kubadziworski.domain.statement;

import com.kubadziworski.bytecodegenerator.StatementGenerator;

/**
 * Created by kuba on 02.04.16.
 */
public interface Statement extends Node {
    public void accept(StatementGenerator generator);
}
