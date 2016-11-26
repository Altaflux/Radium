package com.kubadziworski.util;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

/**
 * Created by plozano on 11/22/2016.
 */
public class BoxUnboxer {

    private static final String NULLABLE_BYTE_TYPE_NAME = "java/lang/Byte";
    private static final String NULLABLE_SHORT_TYPE_NAME = "java/lang/Short";
    private static final String NULLABLE_LONG_TYPE_NAME = "java/lang/Long";


    static void box(Type type, Type toType, InstructionAdapter v) {
        if (type == Type.BYTE_TYPE || toType.getInternalName().equals(NULLABLE_BYTE_TYPE_NAME) && type == Type.INT_TYPE) {
            v.cast(type, Type.BYTE_TYPE);
            v.invokestatic(NULLABLE_BYTE_TYPE_NAME, "valueOf", "(B)L" + NULLABLE_BYTE_TYPE_NAME + ";", false);
        } else if (type == Type.SHORT_TYPE || toType.getInternalName().equals(NULLABLE_SHORT_TYPE_NAME) && type == Type.INT_TYPE) {
            v.cast(type, Type.SHORT_TYPE);
            v.invokestatic(NULLABLE_SHORT_TYPE_NAME, "valueOf", "(S)L" + NULLABLE_SHORT_TYPE_NAME + ";", false);
        } else if (type == Type.LONG_TYPE || toType.getInternalName().equals(NULLABLE_LONG_TYPE_NAME) && type == Type.INT_TYPE) {
            v.cast(type, Type.LONG_TYPE);
            v.invokestatic(NULLABLE_LONG_TYPE_NAME, "valueOf", "(J)L" + NULLABLE_LONG_TYPE_NAME + ";", false);
        } else if (type == Type.INT_TYPE) {
            v.invokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (type == Type.BOOLEAN_TYPE) {
            v.invokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (type == Type.CHAR_TYPE) {
            v.invokestatic("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (type == Type.FLOAT_TYPE) {
            v.invokestatic("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (type == Type.DOUBLE_TYPE) {
            v.invokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }


     static void unbox(Type type, InstructionAdapter v) {
        if (type == Type.INT_TYPE) {
            v.invokevirtual("java/lang/Number", "intValue", "()I", false);
        }
        else if (type == Type.BOOLEAN_TYPE) {
            v.invokevirtual("java/lang/Boolean", "booleanValue", "()Z", false);
        }
        else if (type == Type.CHAR_TYPE) {
            v.invokevirtual("java/lang/Character", "charValue", "()C", false);
        }
        else if (type == Type.SHORT_TYPE) {
            v.invokevirtual("java/lang/Number", "shortValue", "()S", false);
        }
        else if (type == Type.LONG_TYPE) {
            v.invokevirtual("java/lang/Number", "longValue", "()J", false);
        }
        else if (type == Type.BYTE_TYPE) {
            v.invokevirtual("java/lang/Number", "byteValue", "()B", false);
        }
        else if (type == Type.FLOAT_TYPE) {
            v.invokevirtual("java/lang/Number", "floatValue", "()F", false);
        }
        else if (type == Type.DOUBLE_TYPE) {
            v.invokevirtual("java/lang/Number", "doubleValue", "()D", false);
        }
    }
}
