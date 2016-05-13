package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.Scope;

/**
 * Created by kuba on 13.05.16.
 */
public class FieldNotFoundException extends RuntimeException {
    public FieldNotFoundException(Scope scope, String fieldName) {
        super("No field found for name " + fieldName + " found in scope" + scope);
    }
}