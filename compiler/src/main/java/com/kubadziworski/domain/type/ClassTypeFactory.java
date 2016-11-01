package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class ClassTypeFactory {
    private static volatile GlobalScope globalScope;

    private static final Logger logger = LoggerFactory.getLogger(ClassTypeFactory.class);

    public static void initialize(GlobalScope globalScope) {
        synchronized (ClassTypeFactory.class) {
            ClassTypeFactory.globalScope = globalScope;
        }
    }

    public static Type createClassType(String name) {
        if (globalScope != null) {
            Scope scope = globalScope.getScopeByClassName(name);
            if(scope != null) {
                return new EnkelType(name, scope);
            }
        }
        return new JavaClassType(name);
    }

    public static Optional<GlobalScope> getGlobalScope() {
        return Optional.ofNullable(globalScope);
    }
}
