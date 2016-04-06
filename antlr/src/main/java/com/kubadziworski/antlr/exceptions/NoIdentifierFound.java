package com.kubadziworski.antlr.exceptions;

import com.kubadziworski.antlr.domain.scope.Scope;

/**
 * Created by kuba on 06.04.16.
 */
public class NoIdentifierFound extends RuntimeException {
    public NoIdentifierFound(Scope scope, String identifierName) {
        super("No identifier for name " + identifierName + "found in scope" + scope);
    }
}
