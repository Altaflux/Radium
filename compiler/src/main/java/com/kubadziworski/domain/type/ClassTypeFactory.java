package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.ClassNotFoundForNameException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ClassTypeFactory {
    private static volatile GlobalScope globalScope;
    private static final Map<String, Type> syntheticTypes;

    public static ClassLoader classLoader;

    static {
        //TODO DECLARING "radium.Any" on here is needed for now
        //as subclass declaration needs to occur before types are formally loaded
        //I need to check if at this moment GlobalScope really needs a scope
        //given that Types right now encapsulate almost all functionality
        Map<String, Type> typeMap = new HashMap<>();
        typeMap.put("radium.Any", AnyType.INSTANCE);
        typeMap.put("radium.Unit", UnitType.CONCRETE_INSTANCE);
        typeMap.put("radium.Int", PrimitiveTypes.INT_TYPE);
        typeMap.put("radium.Float", PrimitiveTypes.FLOAT_TYPE);
        typeMap.put("radium.Long", PrimitiveTypes.LONG_TYPE);
        typeMap.put("radium.Double", PrimitiveTypes.DOUBLE_TYPE);
        typeMap.put("radium.Short", PrimitiveTypes.SHORT_TYPE);
        typeMap.put("radium.Char", PrimitiveTypes.CHAR_TYPE);
        typeMap.put("radium.Byte", PrimitiveTypes.BYTE_TYPE);
        typeMap.put("radium.Boolean", PrimitiveTypes.BOOLEAN_TYPE);


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
        try {
            return new JavaClassType(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundForNameException(name);
        }
    }

    public static Type createClassType(Class name) {
        if (syntheticTypes.containsKey(name.getCanonicalName())) {
            return syntheticTypes.get(name.getCanonicalName());
        }
        return new JavaClassType(name);
    }

    public static Optional<GlobalScope> getGlobalScope() {
        return Optional.ofNullable(globalScope);
    }
}
