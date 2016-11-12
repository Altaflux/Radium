package com.kubadziworski.domain.type;

import static org.objectweb.asm.Opcodes.*;


/**
 * Created by kuba on 30.04.16.
 */
public enum TypeSpecificOpcodes {

    INT(ILOAD, ISTORE, IRETURN, IADD, ISUB, IMUL, IDIV, DUP, DUP_X1, INEG, true), //values (-127,127) - one byte.
    LONG(LLOAD, LSTORE, LRETURN, LADD, LSUB, LMUL, LDIV, DUP2, DUP2_X1, LNEG, true),
    FLOAT(FLOAD, FSTORE, FRETURN, FADD, FSUB, FMUL, FDIV, DUP, DUP_X1, FNEG, true),
    DOUBLE(DLOAD, DSTORE, DRETURN, DADD, DSUB, DMUL, DDIV, DUP2, DUP2_X1, DNEG, false),
    VOID(ALOAD, ASTORE, RETURN, 0, 0, 0, 0, 0, 0, 0, true),
    OBJECT(ALOAD, ASTORE, ARETURN, 0, 0, 0, 0, DUP, DUP_X1, 0, false);

    private final int load;
    private final int store;
    private final int ret;
    private final int add;
    private final int sub;
    private final int mul;
    private final int div;
    private final int dup;
    private final int dupX1;
    private final int neg;
    private final boolean isPrimitive;

    TypeSpecificOpcodes(int load, int store, int ret, int add, int sub, int mul, int div, int dup, int dupX1, int neg, boolean isPrimitive) {

        this.load = load;
        this.store = store;
        this.ret = ret;
        this.add = add;
        this.sub = sub;
        this.mul = mul;
        this.div = div;
        this.dup = dup;
        this.dupX1 = dupX1;
        this.neg = neg;
        this.isPrimitive = isPrimitive;
    }

    public int getLoad() {
        return load;
    }

    public int getStore() {
        return store;
    }

    public int getReturn() {
        return ret;
    }

    public int getAdd() {
        return add;
    }

    public int getSubstract() {
        return sub;
    }

    public int getMultiply() {
        return mul;
    }

    public int getDivide() {
        return div;
    }

    public int getDupCode() {
        return dup;
    }

    public int getDupX1Code() {
        return dupX1;
    }

    public int getNegation() {
        return neg;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }
}
