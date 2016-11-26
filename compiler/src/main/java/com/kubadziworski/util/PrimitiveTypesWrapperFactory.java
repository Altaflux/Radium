package com.kubadziworski.util;

import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import org.objectweb.asm.commons.InstructionAdapter;

public class PrimitiveTypesWrapperFactory {


    public static void coerce(Type to, Type from, InstructionAdapter v) {

        if(to instanceof TypeProjection){
            to = ((TypeProjection) to).getInternalType();
        }
        if(from instanceof TypeProjection){
            from = ((TypeProjection) from).getInternalType();
        }

        if(from.equals(NullType.INSTANCE)){
            return;
        }

        if (to.equals(from)) return;

        if (from instanceof BoxableType && to instanceof BoxableType) {
            if (((BoxableType) to).isBoxed() && !((BoxableType) from).isBoxed()) {
                BoxUnboxer.box(org.objectweb.asm.Type.getType(from.getDescriptor()), org.objectweb.asm.Type.getType(to.getDescriptor()), v);
                coerce(to, ((BoxableType) from).getBoxedType(), v);
            } else if (!((BoxableType) to).isBoxed() && ((BoxableType) from).isBoxed()) {
                BoxUnboxer.unbox(org.objectweb.asm.Type.getType(((BoxableType) from).getUnBoxedType().getDescriptor()), v);
                coerce(to, ((BoxableType) from).getUnBoxedType(), v);
            }else {
                v.cast(org.objectweb.asm.Type.getType(from.getDescriptor()), org.objectweb.asm.Type.getType(to.getDescriptor()));
            }
        } else if (from.isPrimitive() && !to.isPrimitive()) {
            BoxUnboxer.box(org.objectweb.asm.Type.getType(from.getDescriptor()),
                    org.objectweb.asm.Type.getType(((BoxableType) from).getBoxedType().getDescriptor()), v);
        } else {
            v.cast(org.objectweb.asm.Type.getType(from.getDescriptor()), org.objectweb.asm.Type.getType(to.getDescriptor()));
        }
    }
}
