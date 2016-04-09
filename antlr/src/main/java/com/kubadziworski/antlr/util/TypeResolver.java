package com.kubadziworski.antlr.util;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.domain.type.BultInType;
import com.kubadziworski.antlr.domain.type.ClassType;
import com.kubadziworski.antlr.domain.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public class TypeResolver {
    public static Type getFromTypeName(EnkelParser.TypeContext typeContext) {
        if(typeContext == null) return BultInType.VOID;
        String typeName = typeContext.getText();
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if(builtInType.isPresent()) return builtInType.get();
        return new ClassType(typeName);
    }

    public static Type getFromValue(String value) {
        if(StringUtils.isEmpty(value)) return BultInType.VOID;
        if(StringUtils.isNumeric(value)) {
            return BultInType.INT;
        }
        return BultInType.STRING;
    }

    private static Optional<BultInType> getBuiltInType(String typeName) {
        return Arrays.stream(BultInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
