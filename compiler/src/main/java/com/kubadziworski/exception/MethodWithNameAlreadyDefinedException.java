package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.FunctionSignature;

/**
 * Created by kuba on 08.05.16.
 */
public class MethodWithNameAlreadyDefinedException extends RuntimeException {
    public MethodWithNameAlreadyDefinedException(FunctionSignature signature) {
        super("Method already defined in scope :" + signature);
    }
}
