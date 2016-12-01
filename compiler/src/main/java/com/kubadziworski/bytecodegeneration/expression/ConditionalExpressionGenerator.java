package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

public class ConditionalExpressionGenerator {

    private final InstructionAdapter methodVisitor;

    public ConditionalExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ConditionalExpression conditionalExpression, StatementGenerator statementGenerator) {
        Expression leftExpression = conditionalExpression.getLeftExpression();
        Expression rightExpression = conditionalExpression.getRightExpression();
        CompareSign compareSign = conditionalExpression.getCompareSign();
        if (conditionalExpression.isPrimitiveComparison()) {
            generatePrimitivesComparison(leftExpression, rightExpression, compareSign, statementGenerator);
            return;
        }
        generateObjectsComparison(leftExpression, rightExpression, compareSign, statementGenerator);
        Label endLabel = new Label();
        Label trueLabel = new Label();
        methodVisitor.visitJumpInsn(compareSign.getOpcode(), trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitLabel(endLabel);
    }

    private void generateObjectsComparison(Expression leftExpression, Expression rightExpression, CompareSign compareSign, StatementGenerator statementGenerator) {
        Parameter parameter = new Parameter("o", ClassTypeFactory.createClassType("java.lang.Object"), null);

        List<Parameter> parameters = Collections.singletonList(parameter);
        Argument argument = new Argument(rightExpression, null, AnyType.INSTANCE);
        List<Argument> arguments = Collections.singletonList(argument);
        switch (compareSign) {
            case EQUAL:
            case NOT_EQUAL:
                FunctionSignature equalsSignature = new FunctionSignature("equals", parameters, PrimitiveTypes.BOOLEAN_TYPE, Modifier.PUBLIC, leftExpression.getType());
                FunctionCall equalsCall = new FunctionCall(equalsSignature, arguments, leftExpression);
                equalsCall.accept(statementGenerator);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.IXOR);
                break;
            case LESS:
            case GREATER:
            case LESS_OR_EQUAL:
            case GRATER_OR_EQUAL:
                FunctionSignature compareToSignature = new FunctionSignature("compareTo", parameters, PrimitiveTypes.INT_TYPE, Modifier.PUBLIC, leftExpression.getType());
                FunctionCall compareToCall = new FunctionCall(compareToSignature, arguments, leftExpression);
                compareToCall.accept(statementGenerator);
                break;
        }
    }

//    private void generatePrimitivesComparison(Expression leftExpression, Expression rightExpression, CompareSign compareSign, StatementGenerator statementGenerator) {
//        leftExpression.accept(statementGenerator);
//        rightExpression.accept(statementGenerator);
//        methodVisitor.visitInsn(Opcodes.ISUB);
//    }

    private void generatePrimitivesComparison(Expression leftExpression, Expression rightExpression, CompareSign compareSign, StatementGenerator statementGenerator) {
        InstructionAdapter adapter = methodVisitor;

        Type leftT = leftExpression.getType();
        Type rightT = rightExpression.getType();

        if(leftT instanceof TypeProjection){
            leftT = ((TypeProjection) leftT).getInternalType();
        }
        if(rightT instanceof TypeProjection){
            rightT = ((TypeProjection) rightT).getInternalType();
        }
        AbstractPrimitiveType leftType = (AbstractPrimitiveType) leftT;
        AbstractPrimitiveType rightType = (AbstractPrimitiveType) rightT;
        AbstractPrimitiveType mainType = PrimitiveTypes.getBiggerDenominator(leftType, rightType);

        leftExpression.accept(statementGenerator);
        PrimitiveTypesWrapperFactory.coerce(mainType.getUnBoxedType(), leftType, adapter);

        rightExpression.accept(statementGenerator);
        PrimitiveTypesWrapperFactory.coerce(mainType.getUnBoxedType(), rightType, adapter);

        mainType.compare(compareSign, methodVisitor);
    }
}