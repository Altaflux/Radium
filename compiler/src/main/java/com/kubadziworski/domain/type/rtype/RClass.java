package com.kubadziworski.domain.type.rtype;

import com.kubadziworski.bytecodegeneration.inline.CodeInliner;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.RField;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by plozano on 4/6/2017.
 */
public interface RClass {

    String getName();

    default String getPackage() {
        String clazz = getName();
        final int i = clazz.lastIndexOf('.');
        if (i != -1) {
            return clazz.substring(0, i);
        } else {
            return StringUtils.EMPTY;
        }
    }

    Optional<RType> getSuperType();

    List<RType> getInterfaces();

    Type.ClassType getClassType();

    List<RField> getFields();

    List<FunctionSignature> getFunctionSignatures();

    List<FunctionSignature> getConstructorSignatures();

    int inheritsFrom(RClass type);

    Optional<Type> nearestDenominator(Type type);

    boolean isPrimitive();

    Type.Nullability isNullable();

    org.objectweb.asm.Type getAsmType();

    default CodeInliner getInliner() {
        throw new UnsupportedOperationException("Type: " + getClass() + " does not supports inlining");
    }

    String getQualifiedName();

    String getSimpleName();

    default String getIdentifier() {
        return "$";
    }

}
