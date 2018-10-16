package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.intrinsics.IntrinsicMethods;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.function.FunctionCall;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;
import radium.jvm.internal.Intrinsics;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class ConditionalExpressionGenerator {

    private final InstructionAdapter methodVisitor;
    private static ThreadLocal<IntrinsicMethods> intrinsicMethods = ThreadLocal.withInitial(IntrinsicMethods::new);

    private static final FunctionSignature NULLABLE_EQUALS = new FunctionSignature("areEqual",
            Arrays.asList(new Parameter("o", AnyType.INSTANCE, null), new Parameter("o", AnyType.INSTANCE, null)), PrimitiveTypes.BOOLEAN_TYPE,
            Modifiers.empty().with(Modifier.PUBLIC).with(Modifier.STATIC),
            new JavaClassType(Intrinsics.class), SignatureType.FUNCTION_CALL);

    public ConditionalExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ConditionalExpression conditionalExpression, StatementGenerator statementGenerator) {
        Expression leftExpression = conditionalExpression.getLeftExpression();
        Expression rightExpression = conditionalExpression.getRightExpression();
        CompareSign compareSign = conditionalExpression.getCompareSign();


        Optional<Expression> intrinsic = signature(compareSign, leftExpression, rightExpression);
        if (intrinsic.isPresent()) {
            intrinsic.get().accept(statementGenerator);
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
        switch (compareSign) {
            case EQUAL:
            case NOT_EQUAL:
                if (leftExpression.getType().isNullable().equals(Type.Nullability.NULLABLE)) {
                    FunctionCall functionCall = new FunctionCall(NULLABLE_EQUALS,
                            NULLABLE_EQUALS.createArgumentList(Arrays.asList(new ArgumentHolder(rightExpression, null), new ArgumentHolder(leftExpression, null))),
                            new EmptyExpression(new JavaClassType(Intrinsics.class)));

                    functionCall.accept(statementGenerator);
                } else {
                    FunctionSignature equalsSignature = leftExpression.getType().getMethodCallSignature("equals", Collections.singletonList(new ArgumentHolder(rightExpression, null)));
                    FunctionCall equalsCall = new FunctionCall(equalsSignature, equalsSignature.createArgumentList(Collections.singletonList(new ArgumentHolder(rightExpression, null))), leftExpression);
                    equalsCall.accept(statementGenerator);
                }
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.IXOR);
                break;
            case LESS:
            case GREATER:
            case LESS_OR_EQUAL:
            case GRATER_OR_EQUAL:
                FunctionSignature compareToSignature = leftExpression.getType().getMethodCallSignature("compareTo", Collections.singletonList(new ArgumentHolder(rightExpression, null)));
                FunctionCall compareToCall = new FunctionCall(compareToSignature, compareToSignature.createArgumentList(Collections.singletonList(new ArgumentHolder(rightExpression, null))), leftExpression);
                compareToCall.accept(statementGenerator);
                break;
        }
    }

    private Optional<Expression> signature(CompareSign sign, Expression leftExpression, Expression rightExpression) {
        FunctionSignature signature = new FunctionSignature(sign.getSign(), Collections.singletonList(new Parameter("o", rightExpression.getType(), null))
                , PrimitiveTypes.BOOLEAN_TYPE, Modifiers.empty().with(Modifier.PUBLIC), leftExpression.getType(), SignatureType.FUNCTION_CALL);
        FunctionCall functionCall = new FunctionCall(signature, signature.createArgumentList(Collections.singletonList(new ArgumentHolder(rightExpression, null))),
                leftExpression);
        return intrinsicMethods.get().intrinsicMethod(functionCall).map(intrinsicMethod -> intrinsicMethod.toExpression(functionCall, methodVisitor));

    }
}