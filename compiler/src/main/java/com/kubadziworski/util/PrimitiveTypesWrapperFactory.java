package com.kubadziworski.util;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import org.objectweb.asm.commons.InstructionAdapter;

public class PrimitiveTypesWrapperFactory {


    public static void coerce(Type to, Type from, InstructionAdapter v) {

        if (to instanceof TypeProjection) {
            to = ((TypeProjection) to).getInternalType();
        }
        if (from instanceof TypeProjection) {
            from = ((TypeProjection) from).getInternalType();
        }

        if (from.equals(NullType.INSTANCE)) {
            return;
        }

        if (to.equals(from)) return;

        if (from instanceof BoxableType && to instanceof BoxableType) {
            if (((BoxableType) to).isBoxed() && !((BoxableType) from).isBoxed()) {
                BoxUnboxer.box(from.getAsmType(), to.getAsmType(), v);
                coerce(to, ((BoxableType) from).getBoxedType(), v);
            } else if (!((BoxableType) to).isBoxed() && ((BoxableType) from).isBoxed()) {
                BoxUnboxer.unbox(((BoxableType) from).getUnBoxedType().getAsmType(), v);
                coerce(to, ((BoxableType) from).getUnBoxedType(), v);
            } else {
                v.cast(from.getAsmType(), to.getAsmType());
            }
        } else if (from.isPrimitive() && !to.isPrimitive()) {
            BoxUnboxer.box(from.getAsmType(),
                    ((BoxableType) from).getBoxedType().getAsmType(), v);
        } else {
            v.cast(from.getAsmType(), to.getAsmType());
        }
    }


    public static boolean isPrimitiveType(Type type) {
        if (type instanceof TypeProjection) {
            type = ((TypeProjection) type).getInternalType();
        }
        return type instanceof BoxableType && !((BoxableType) type).isBoxed();
    }

    public static Value primitiveDummyValue(Type type) {
        if (type instanceof TypeProjection) {
            type = ((TypeProjection) type).getInternalType();
        }
        return ((AbstractPrimitiveType) type).primitiveDummyValue();
    }
}
