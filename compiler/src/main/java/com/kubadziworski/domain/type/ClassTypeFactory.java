package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.GlobalScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class ClassTypeFactory {
    private static volatile GlobalScope globalScope;

    private static final Logger logger = LoggerFactory.getLogger(ClassTypeFactory.class);

    public static void initialize(GlobalScope globalScope) {
        synchronized (ClassTypeFactory.class) {
//            if (ClassTypeFactory.globalScope == null) {
//                logger.warn("The globalScope instance has already been assigned", new RuntimeException());
//            }
            ClassTypeFactory.globalScope = globalScope;
        }
    }

    public static ClassType createClassType(String name) {
        if (globalScope != null) {
            return new ClassType(name, globalScope.getScopeByClassName(name));
        }
        return new ClassType(name);
    }

    public static Optional<GlobalScope> getGlobalScope() {
        return Optional.ofNullable(globalScope);
    }
}
