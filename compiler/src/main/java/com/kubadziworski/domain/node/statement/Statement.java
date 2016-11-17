package com.kubadziworski.domain.node.statement;

import com.kubadziworski.domain.node.Node;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.RdElement;

/**
 * Created by kuba on 02.04.16.
 */
@FunctionalInterface
public interface Statement extends Node {
    void accept(StatementGenerator generator);

    default boolean isReturnComplete() {
        return false;
    }
}
