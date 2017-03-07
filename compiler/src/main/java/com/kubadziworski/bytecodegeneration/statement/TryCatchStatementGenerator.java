package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.trycatch.CatchBlock;
import com.kubadziworski.domain.node.expression.trycatch.TryCatchExpression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

public class TryCatchStatementGenerator {

    private final InstructionAdapter methodVisitor;

    private static final String FINALLY_THROWABLE_NAME = "$$Throw";

    public TryCatchStatementGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }


    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator statementGenerator) {
        generateStuff(tryCatchStatement.getFinallyBlock().orElse(null), tryCatchStatement.getStatement(), Collections.emptyList(), statementGenerator, VoidType.INSTANCE);
    }

    public void generate(TryCatchExpression tryCatchStatement, StatementGenerator statementGenerator) {
        generateStuff(tryCatchStatement.getFinallyBlock().orElse(null), tryCatchStatement.getStatement(), tryCatchStatement.getCatchBlocks(), statementGenerator, tryCatchStatement.getType());
    }

    private void generateStuff(Block finalBlock, Statement tryExpression, List<CatchBlock> catchBlocks, StatementGenerator statementGenerator, Type expectedType) {
        Label startOfTryBlock = new Label();
        Label endOfTryBlock = new Label();
        Label fullEndOfTryCatchStatementLabel = new Label();
        methodVisitor.visitLabel(startOfTryBlock);


        TryCatchFilter finalGenerator = new TryCatchFilter(finalBlock, startOfTryBlock, null, statementGenerator, statementGenerator.getScope());

        tryExpression.accept(finalGenerator);

        if (!tryExpression.isReturnComplete()) {
            methodVisitor.visitJumpInsn(GOTO, fullEndOfTryCatchStatementLabel);
        }
        methodVisitor.visitLabel(endOfTryBlock);

        if (finalGenerator.labelPacks.isEmpty()) {
            finalGenerator.labelPacks.add(new LabelPack(startOfTryBlock, endOfTryBlock));
        }

        List<CatchPack> catchLabels = catchBlocks.stream()
                .map(catchBlock -> {
                    Label catchStartLabel = new Label();
                    methodVisitor.visitLabel(catchStartLabel);
                    FunctionScope catchBlockScope = catchBlock.getBlock().getScope();
                    methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(catchBlock.getParameter().getName()));

                    List<LabelPack> labelPacks = new ArrayList<>();

                    if (finalBlock != null) {
                        TryCatchFilter catchGenerator = new TryCatchFilter(finalBlock, catchStartLabel, null, statementGenerator, statementGenerator.getScope());
                        catchBlock.getBlock().accept(catchGenerator);
                        labelPacks.addAll(catchGenerator.labelPacks);
                    } else {
                        catchBlock.getBlock().accept(statementGenerator);
                    }

                    if (!catchBlock.isReturnComplete()) {

                        //If the whole tryCatch expression is of Unit type then we should not create a Unit instance,
                        //assignment and parameter declarations must instantiate their own Unit instances.
                        if (catchBlock.getType().equals(VoidType.INSTANCE) && !expectedType.equals(VoidType.INSTANCE)) {
                            UnitType.expression().accept(statementGenerator);
                        }
                        methodVisitor.visitJumpInsn(GOTO, fullEndOfTryCatchStatementLabel);
                    }
                    return new CatchPack(catchStartLabel, catchBlock.getParameter().getType(), labelPacks);
                }).collect(Collectors.toList());


        catchLabels.forEach(label -> finalGenerator.labelPacks
                .forEach(labelPack ->
                        methodVisitor.visitTryCatchBlock(labelPack.firstLabel, labelPack.lastLabel,
                                label.firstLabel, label.type.getAsmType().getInternalName())));


        if (finalBlock != null) {
            Label finalLabel = new Label();
            methodVisitor.visitLabel(finalLabel);


            FunctionScope catchBlockScope = finalBlock.getScope();
            catchBlockScope.addLocalVariable(new LocalVariable(FINALLY_THROWABLE_NAME, ClassTypeFactory.createClassType("java.lang.Throwable"), true));
            methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(FINALLY_THROWABLE_NAME));
            finalBlock.accept(statementGenerator);

            if (!finalBlock.isReturnComplete()) {
                methodVisitor.visitVarInsn(ALOAD, catchBlockScope.getLocalVariableIndex(FINALLY_THROWABLE_NAME));
                methodVisitor.visitInsn(ATHROW);
            }

            Stream.concat(catchLabels.stream().map(catchPack -> catchPack.labelPacks).flatMap(Collection::stream), finalGenerator.labelPacks.stream())
                    .forEach(labelPack -> methodVisitor.visitTryCatchBlock(labelPack.firstLabel, labelPack.lastLabel, finalLabel, null));

        }
        methodVisitor.visitLabel(fullEndOfTryCatchStatementLabel);
    }


    private class TryCatchFilter extends StatementGeneratorFilter {

        private AtomicReference<Label> startLabel = new AtomicReference<>();
        private final List<LabelPack> labelPacks;
        private final Block finalBlock;
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        TryCatchFilter(Block finalBlock, Label startLabel, StatementGenerator parent, StatementGenerator next, FunctionScope scope) {
            super(parent, next, scope);
            this.startLabel.set(startLabel);
            labelPacks = new ArrayList<>();
            this.finalBlock = finalBlock;
        }

        private TryCatchFilter(Block finalBlock, List<LabelPack> labelPacks, AtomicReference<Label> startLabel,
                               StatementGenerator parent, StatementGenerator next, FunctionScope scope) {
            super(parent, next, scope);
            this.startLabel = startLabel;
            this.labelPacks = labelPacks;
            this.finalBlock = finalBlock;
        }

        @Override
        public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
            returnStatement.getExpression().accept(generator);

            Label endOfReturnLabel = new Label();
            methodVisitor.visitLabel(endOfReturnLabel);
            labelPacks.add(new LabelPack(startLabel.get(), endOfReturnLabel));

            org.objectweb.asm.Type asmType = org.objectweb.asm.Type.getType(getScope().getCurrentFunctionSignature().getReturnType().getAsmType().getDescriptor());
            if (finalBlock != null) {
                String varName = "$$Return" + atomicInteger.incrementAndGet();
                LocalVariable returnVariable = new LocalVariable(varName, returnStatement.getExpression().getType());
                getScope().addLocalVariable(returnVariable);
                methodVisitor.visitVarInsn(returnStatement.getExpression().getType().getAsmType().getOpcode(Opcodes.ISTORE), getScope().getLocalVariableIndex(varName));

                StatementGeneratorFilter filter = new StatementGeneratorFilter(null, next, getScope());
                finalBlock.accept(filter);

                if (!finalBlock.isReturnComplete()) {
                    next.generate(new ReturnStatement(new LocalVariableReference(returnVariable)), next);
                }
            } else {
                PrimitiveTypesWrapperFactory.coerce(getScope().getCurrentFunctionSignature().getReturnType(), returnStatement.getExpression().getType(),
                        methodVisitor);
                methodVisitor.visitInsn(asmType.getOpcode(IRETURN));
            }

            Label nextReturnStartLabel = new Label();
            methodVisitor.visitLabel(nextReturnStartLabel);
            startLabel.set(nextReturnStartLabel);
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new TryCatchFilter(finalBlock, labelPacks, startLabel, parent, this.next, getScope());
        }
    }

    private static class LabelPack {
        Label firstLabel;
        Label lastLabel;

        LabelPack(Label firstLabel, Label lastLabel) {
            this.firstLabel = firstLabel;
            this.lastLabel = lastLabel;
        }
    }

    private static class CatchPack {
        Label firstLabel;
        Type type;
        List<LabelPack> labelPacks;

        CatchPack(Label firstLabel, Type type, List<LabelPack> labelPacks) {
            this.firstLabel = firstLabel;
            this.type = type;
            this.labelPacks = labelPacks;
        }
    }

}
