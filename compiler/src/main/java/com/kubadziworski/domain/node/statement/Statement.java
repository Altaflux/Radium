package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.RdElement;

/**
 * Created by kuba on 02.04.16.
 */
public interface Statement extends RdElement {

    void accept(StatementGenerator generator);

    default boolean isReturnComplete() {
        return false;
    }
}
