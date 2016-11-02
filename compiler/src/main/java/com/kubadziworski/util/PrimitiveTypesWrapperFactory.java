package com.kubadziworski.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;

import java.util.Map;
import java.util.Optional;

/**
 * Created by kuba on 17.05.16.
 */
public class PrimitiveTypesWrapperFactory {

    private final static BiMap<BuiltInType,Type> types = ImmutableBiMap.of(
            BuiltInType.INT, DefaultTypes.Integer(),
            BuiltInType.BOOLEAN,DefaultTypes.Boolean(),
            BuiltInType.FLOAT,DefaultTypes.Float(),
            BuiltInType.DOUBLE,DefaultTypes.Double()
    );

    private final static Map<Type,String> toPrimitiveMethodName = ImmutableMap.of(
            DefaultTypes.Integer(),"intValue"
    );

    public static Optional<BuiltInType> getPrimitiveForWrapper(Type type) {
        return Optional.ofNullable(types.inverse().get(type));
    }

    public static Optional<Type> getWrapperForPrimitive(Type type) {
        return Optional.ofNullable(types.get(type));
    }

}
