package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.Type;

import java.util.Optional;


public class EnkelScope {

    private final GlobalScope globalScope;


    public EnkelScope(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }


    public Optional<com.kubadziworski.domain.scope.Field> getFieldSignature(Type owner, String fieldName) {
        try {
            return Optional.of(globalScope.getScopeByClassName(owner.getName()).getField(fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
