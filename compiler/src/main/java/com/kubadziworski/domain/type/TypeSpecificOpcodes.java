package com.kubadziworski.domain.type;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by kuba on 30.04.16.
 */
public enum TypeSpecificOpcodes {

    INT(ISTORE, IRETURN, DUP, DUP_X1, true), //values (-127,127) - one byte.
    LONG( LSTORE, LRETURN, DUP2, DUP2_X1, true),
    FLOAT( FSTORE, FRETURN, DUP, DUP_X1, true),
    DOUBLE( DSTORE, DRETURN, DUP2, DUP2_X1, true),
    VOID(ASTORE, RETURN, 0, 0, true),
    OBJECT(ASTORE, ARETURN, DUP, DUP_X1, false);


    private final int store;
    private final int ret;
    private final int dup;
    private final int dupX1;
    private final boolean isPrimitive;

    TypeSpecificOpcodes( int store, int ret, int dup, int dupX1, boolean isPrimitive) {
        this.store = store;
        this.ret = ret;
        this.dup = dup;
        this.dupX1 = dupX1;
        this.isPrimitive = isPrimitive;
    }


    public int getStore() {
        return store;
    }

    public int getReturn() {
        return ret;
    }

    public int getDupCode() {
        return dup;
    }

    public int getDupX1Code() {
        return dupX1;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }
}
