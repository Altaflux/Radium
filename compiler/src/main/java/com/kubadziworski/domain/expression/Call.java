package com.kubadziworski.domain.expression;

import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
public interface Call extends Expression {
    List<Expression> getArguments();
    String getIdentifier();
}
