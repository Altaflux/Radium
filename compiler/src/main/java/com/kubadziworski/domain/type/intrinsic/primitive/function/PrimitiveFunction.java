package com.kubadziworski.domain.type.intrinsic.primitive.function;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.arthimetic.PureArithmeticExpression;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;


public class PrimitiveFunction {

    public static void compareFloat(CompareSign compareSign, MethodVisitor methodVisitor) {
        callCompareFloatOrDouble(compareSign, methodVisitor, Type.FLOAT_TYPE);
    }

    private static void callCompareFloatOrDouble(CompareSign compareSign, MethodVisitor methodVisitor, Type type) {
        switch (compareSign) {
            case LESS: {
                methodVisitor.visitInsn(type == Type.FLOAT_TYPE ? Opcodes.FCMPG : Opcodes.DCMPG);
                break;
            }
            case LESS_OR_EQUAL: {
                methodVisitor.visitInsn(type == Type.FLOAT_TYPE ? Opcodes.FCMPG : Opcodes.DCMPG);
                break;
            }
            default: {
                methodVisitor.visitInsn(type == Type.FLOAT_TYPE ? Opcodes.FCMPL : Opcodes.DCMPL);
                break;
            }
        }
        callCompareSign(compareSign, methodVisitor);
    }

    public static void compareDouble(CompareSign compareSign, MethodVisitor methodVisitor) {
        callCompareFloatOrDouble(compareSign, methodVisitor, Type.DOUBLE_TYPE);
    }

    public static void compareLong(CompareSign compareSign, MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(Opcodes.LCMP);
        callCompareSign(compareSign, methodVisitor);
    }


    private static void callCompareSign(CompareSign compareSign, MethodVisitor methodVisitor) {
        Label label = new Label();
        Label label2 = new Label();

        methodVisitor.visitInsn(Opcodes.LCMP);
        switch (compareSign) {
            case LESS: {
                methodVisitor.visitJumpInsn(Opcodes.IFGE, label);
                break;
            }
            case LESS_OR_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IFGT, label);
                break;
            }
            case GREATER: {
                methodVisitor.visitJumpInsn(Opcodes.IFLE, label);
                break;
            }
            case GRATER_OR_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IFLT, label);
                break;
            }
            case EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
                break;
            }
            case NOT_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IFEQ, label);
                break;
            }
        }
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, label2);
        methodVisitor.visitLabel(label);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitLabel(label2);
    }


    public static void compareIntType(CompareSign compareSign, MethodVisitor methodVisitor) {

        Label label = new Label();
        Label label2 = new Label();

        switch (compareSign) {
            case LESS: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGE, label);
                break;
            }
            case LESS_OR_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGT, label);
                break;
            }
            case GREATER: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPLE, label);
                break;
            }
            case GRATER_OR_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPLT, label);
                break;
            }
            case EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, label);
                break;
            }
            case NOT_EQUAL: {
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPEQ, label);
                break;
            }
        }
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, label2);
        methodVisitor.visitLabel(label);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitLabel(label2);
    }


    public static void executePrimitiveExpression(FunctionCall functionCall, StatementGenerator statementGenerator, MethodVisitor visitor) {
        switch (functionCall.getIdentifier()) {
            case "minus":
            case "div":
            case "times":
            case "plus": {
                callArithmeticExpression(functionCall, statementGenerator);
                break;
            }
            case "toInt": {
                toInt(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toLong": {
                toLong(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toFloat": {
                toFloat(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toDouble": {
                toDouble(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toChar": {
                toChar(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toByte": {
                toByte(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            case "toShort": {
                toShort(functionCall.getOwner(), visitor, statementGenerator);
                break;
            }
            /////////////////////
            case "toString": {
                generateToString(visitor, functionCall.getOwner(), statementGenerator);
                break;
            }
            case "compareTo": {
                comparePrimitives(visitor, functionCall, statementGenerator);
                break;
            }
        }
    }


    private static void comparePrimitives(MethodVisitor methodVisitor, FunctionCall expression, StatementGenerator generator) {
        InstructionAdapter v = new InstructionAdapter(methodVisitor);
        AbstractPrimitiveType owner = (AbstractPrimitiveType) expression.getOwner().getType();
        AbstractPrimitiveType compareValue = (AbstractPrimitiveType) expression.getArguments().get(0).getType();

        AbstractPrimitiveType topType = PrimitiveTypes.getBiggerDenominator(owner, compareValue);
        Type asmTopType = topType.getAsmType();

        expression.getOwner().accept(generator);
        PrimitiveTypesWrapperFactory.coerce(topType.getUnBoxedType(), owner, v);

        expression.getArguments().get(0).accept(generator);
        PrimitiveTypesWrapperFactory.coerce(topType.getUnBoxedType(), compareValue, v);

        if (asmTopType.equals(Type.INT_TYPE)) {
            v.invokestatic("radium/jvm/internal/Intrinsics", "compare", "(II)I", false);
        } else if (asmTopType.equals(Type.LONG_TYPE)) {
            v.invokestatic("radium/jvm/internal/Intrinsics", "compare", "(JJ)I", false);
        } else if (asmTopType.equals(Type.FLOAT_TYPE)) {
            v.invokestatic("java/lang/Float", "compare", "(II)I", false);
        } else if (asmTopType.equals(Type.DOUBLE_TYPE)) {
            v.invokestatic("java/lang/Double", "compare", "(II)I", false);
        } else {
            throw new UnsupportedOperationException("Invalid types used for comparison");
        }
    }


    private static void callArithmeticExpression(FunctionCall functionCall, StatementGenerator statementGenerator) {
        Expression owner = functionCall.getOwner();
        ArithmeticOperator operator = ArithmeticOperator.fromMethodName(functionCall.getIdentifier());
        List<Argument> arguments = functionCall.getArguments();
        new PureArithmeticExpression(owner, arguments.get(0), functionCall.getType(), operator).accept(statementGenerator);
    }


    private static void generateToString(MethodVisitor methodVisitor, Expression expression, StatementGenerator generator) {
        expression.accept(generator);
        Type type = stringValueOfType(expression.getType().getAsmType());
        InstructionAdapter ad = new InstructionAdapter(methodVisitor);
        ad.invokestatic("java/lang/String", "valueOf", "(" + type.getDescriptor() + ")Ljava/lang/String;", false);
    }

    public static Type stringValueOfType(Type type) {
        int sort = type.getSort();
        return sort == Type.OBJECT || sort == Type.ARRAY
                ? Type.getType(Object.class)
                : sort == Type.BYTE || sort == Type.SHORT ? Type.INT_TYPE : type;
    }

    public static void compareBoolType(CompareSign compareSign, MethodVisitor methodVisitor) {
        compareIntType(compareSign, methodVisitor);
    }
    ///////////////////////////////

    private static void toInt(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.INT_TYPE, value.getType(), ad);
    }

    private static void toLong(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.LONG_TYPE, value.getType(), ad);
    }

    private static void toFloat(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.FLOAT_TYPE, value.getType(), ad);
    }

    private static void toDouble(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.FLOAT_TYPE, value.getType(), ad);
    }

    private static void toChar(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.CHAR_TYPE, value.getType(), ad);
    }

    private static void toByte(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.BYTE_TYPE, value.getType(), ad);
    }

    private static void toShort(Expression value, MethodVisitor v, StatementGenerator generator) {
        InstructionAdapter ad = new InstructionAdapter(v);
        value.accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.SHORT_TYPE, value.getType(), ad);
    }


}
