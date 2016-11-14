package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.UnitType;

import java.util.*;


public class ClassTypeFactory {
    private static volatile GlobalScope globalScope;
    private static final Map<String, Type> syntheticTypes;

    static {
        //TODO DECLARING "radium.Any" on here is needed for now
        //as subclass declaration needs to occur before types are formally loaded
        //I need to check if at this moment GlobalScope really needs a scope
        //given that Types right now encapsulate almost all functionality
        Map<String, Type> typeMap = new HashMap<>();
        typeMap.put("radium.Any", AnyType.INSTANCE);
        typeMap.put("radium.Unit", UnitType.INSTANCE);
        syntheticTypes = Collections.unmodifiableMap(typeMap);
    }

    public static void initialize(GlobalScope globalScope) {
        synchronized (ClassTypeFactory.class) {
            ClassTypeFactory.globalScope = globalScope;
        }
    }

    public static Type createClassType(String name) {
        if (syntheticTypes.containsKey(name)) {
            return syntheticTypes.get(name);
        }

        if (globalScope != null) {
            Scope scope = globalScope.getScopeByClassName(name);
            if (scope != null) {
                return new EnkelType(name, scope);
            }
        }
        return new JavaClassType(name);
    }

    public static Optional<GlobalScope> getGlobalScope() {
        return Optional.ofNullable(globalScope);
    }
}
