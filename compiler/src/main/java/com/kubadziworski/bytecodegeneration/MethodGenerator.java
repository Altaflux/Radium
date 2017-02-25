package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
import com.kubadziworski.bytecodegeneration.util.PropertyAccessorsGenerator;
import com.kubadziworski.domain.*;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.statement.*;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.util.DescriptorFactory;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MethodGenerator {
    private final ClassVisitor cv;

    private static Type DEFAULT_MARKER = ClassTypeFactory.createClassType("radium.internal.DefaultConstructorMarker");


    public MethodGenerator(ClassVisitor cv) {
        this.cv = cv;
    }

    public void generate(Function function) {
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        Scope scope = block.getScope();

        int mod = ModifierTransformer.transform(function.getModifiers());
        MethodVisitor mvs = cv.visitMethod(mod, name, description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateInlineAnnotation(function, mv);
        generateMutabilityAnnotations(function, mv);

        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        block.accept(statementScopeGenerator);
        appendReturnIfNotExists(function, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        createSyntheticDefaults(function);
    }


    private void createSyntheticDefaults(Function function) {

        boolean shouldCreate = function.getParameters().stream().anyMatch(parameter -> parameter.getDefaultValue().isPresent());
        if (!shouldCreate) {
            return;
        }
        boolean allOptional = function.getParameters().stream().allMatch(parameter -> parameter.getDefaultValue().isPresent());

        if (allOptional) {
            allEmpty(function);
        }

        List<Parameter> parameterList = new ArrayList<>(function.getFunctionSignature().getParameters());
        Parameter bitMask = new Parameter("mask", PrimitiveTypes.INT_TYPE, null);
        Parameter markerParam = new Parameter("marker", DEFAULT_MARKER, null);
        parameterList.add(bitMask);
        parameterList.add(markerParam);

        String description = DescriptorFactory.getMethodDescriptor(parameterList, function.getReturnType());
        String name = function.getName();
        int mod = ModifierTransformer.transform(function.getModifiers().with(Modifier.SYNTHETIC));
        MethodVisitor mvs = cv.visitMethod(mod, name, description, null, null);
        InstructionAdapter adapter = new InstructionAdapter(mvs);
        Scope originalScope = ((EnkelType) function.getFunctionSignature().getOwner()).getScope();
        Scope scope = originalScope.cloneWithoutVariables();

        if (!function.getModifiers().contains(Modifier.STATIC)) {
            scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        }

        FunctionGenerator functionGenerator = new FunctionGenerator(scope);
        functionGenerator.addParametersAsLocalVariables(function.getFunctionSignature());
        scope.addLocalVariable(new LocalVariable(bitMask.getName(), bitMask.getType()));
        scope.addLocalVariable(new LocalVariable(markerParam.getName(), markerParam.getType()));

        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(adapter, scope);


        ArgumentHolder argument = new ArgumentHolder(new LocalVariableReference(scope.getLocalVariable("mask")), null);
        FunctionSignature signature = PrimitiveTypes.INT_TYPE.getMethodCallSignature(ArithmeticOperator.BINAND.getMethodName(), Collections.singletonList(argument));

        for (int x = 0; x < function.getFunctionSignature().getParameters().size(); x++) {
            Parameter parameter = function.getFunctionSignature().getParameters().get(x);
            if (!parameter.getDefaultValue().isPresent()) {
                continue;
            }

            Value bitCheckValue = new Value(PrimitiveTypes.INT_TYPE, x);
            FunctionCall andCompare = new FunctionCall(signature, signature.createArgumentList(Collections.singletonList(argument)), bitCheckValue);
            Assignment assignment = new Assignment(scope.getLocalVariable(parameter.getName()), parameter.getDefaultValue().get());
            IfStatement ifStatement = new IfStatement(andCompare, assignment);
            ifStatement.accept(statementScopeGenerator);
        }

        List<Argument> functionArguments = function.getFunctionSignature().getParameters().stream()
                .map(parameter -> new Argument(new LocalVariableReference(scope.getLocalVariable(parameter.getName())), parameter.getName(), parameter.getType()))
                .collect(Collectors.toList());

        Expression owner = scope.isLocalVariableExists("this") ? new LocalVariableReference(scope.getLocalVariable("this"))
                : new EmptyExpression(scope.getClassType());

        FunctionCall methodCall = new FunctionCall(function.getFunctionSignature(), functionArguments, owner);
        ReturnStatement returnStatement = new ReturnStatement(methodCall);
        returnStatement.accept(statementScopeGenerator);

        adapter.visitMaxs(-1, -1);
        adapter.visitEnd();
    }

    private void allEmpty(Function originalFunction) {
        FunctionSignature originalFunctionSignature = originalFunction.getFunctionSignature();
//        List<Parameter> parameterList = new ArrayList<>(originalFunction.getFunctionSignature().getParameters());

        String description = DescriptorFactory.getMethodDescriptor(Collections.emptyList(), originalFunctionSignature.getReturnType());
        String name = originalFunctionSignature.getName();
        int mod = ModifierTransformer.transform(originalFunctionSignature.getModifiers());
        MethodVisitor mvs = cv.visitMethod(mod, name, description, null, null);
        InstructionAdapter adapter = new InstructionAdapter(mvs);

        if (!originalFunctionSignature.getModifiers().contains(Modifier.STATIC)) {
            adapter.visitVarInsn(Opcodes.ALOAD, 0);
        }

        int mask = 0;
        for (int x = 0; x < originalFunctionSignature.getParameters().size(); x++) {
            Parameter parameter = originalFunctionSignature.getParameters().get(x);
            mask = mask | (1 << x);
            if (PrimitiveTypesWrapperFactory.isPrimitiveType(parameter.getType())) {
                Value value = ((AbstractPrimitiveType) parameter.getType()).primitiveDummyValue();
                mvs.visitLdcInsn(value.getValue());
            } else {
                mvs.visitInsn(Opcodes.ACONST_NULL);
            }
        }
        mvs.visitLdcInsn(mask);
        mvs.visitInsn(Opcodes.ACONST_NULL);

        FunctionSignature syntheticSign = getDefaultSignature(originalFunctionSignature);
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(syntheticSign);
        String ownerDescriptor = syntheticSign.getOwner().getAsmType().getInternalName();
        mvs.visitMethodInsn(syntheticSign.getInvokeOpcode(), ownerDescriptor, syntheticSign.getName(), methodDescriptor, false);

        int returnOpCode = syntheticSign.getReturnType().getAsmType().getOpcode(Opcodes.IRETURN);
        mvs.visitInsn(returnOpCode);
        adapter.visitMaxs(-1, -1);
        adapter.visitEnd();
    }

    private static FunctionSignature getDefaultSignature(FunctionSignature functionSignature) {

        List<Parameter> parameterList = new ArrayList<>(functionSignature.getParameters());
        Parameter bitMask = new Parameter("mask", PrimitiveTypes.INT_TYPE, null);
        Parameter markerParam = new Parameter("marker", DEFAULT_MARKER, null);
        parameterList.add(bitMask);
        parameterList.add(markerParam);

        return new FunctionSignature(functionSignature.getName(), parameterList, functionSignature.getReturnType(),
                functionSignature.getModifiers(), functionSignature.getOwner());
    }

    public void generatePropertyAccessor(Function function, Field field) {
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        Scope scope = block.getScope();
        MethodVisitor mvs = cv.visitMethod(ModifierTransformer.transform(function.getModifiers()), name, description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateMutabilityAnnotations(function, mv);

        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        StatementGenerator filteringGenerator = new AccessorInterceptorFilter(field, mv, null, statementScopeGenerator, scope);

        block.accept(filteringGenerator);
        appendReturnIfNotExists(function, block, filteringGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public void generate(Constructor constructor) {
        Block block = (Block) constructor.getRootStatement();
        Scope scope = block.getScope();
        String description = DescriptorFactory.getMethodDescriptor(constructor);
        MethodVisitor mvs = cv.visitMethod(ModifierTransformer.transform(constructor.getModifiers()), "<init>", description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateMutabilityAnnotations(constructor, mv);
        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        FunctionSignature signature = scope.getMethodCallSignature(SuperCall.SUPER_IDENTIFIER, Collections.emptyList());
        new SuperCall(signature).accept(statementScopeGenerator);
        block.accept(statementScopeGenerator);
        appendReturnIfNotExists(constructor, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void generateInlineAnnotation(Function function, InstructionAdapter mv) {
        if (RadiumModifiers.isInline(ModifierTransformer.transform(function.getModifiers()))) {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/internal/InlineOnly;", false);
            av0.visitEnd();
        }
    }

    private void generateMutabilityAnnotations(Function function, InstructionAdapter mv) {
        if (function.getReturnType().isNullable().equals(Type.Nullability.NULLABLE)) {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/annotations/Nullable;", false);
            av0.visitEnd();
        } else {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/annotations/NotNull;", false);
            av0.visitEnd();
        }

        IntStream.range(0, function.getParameters().size()).forEach(index -> {
            Parameter parameter = function.getParameters().get(index);
            if (parameter.getType().isNullable().equals(Type.Nullability.NULLABLE) || parameter.getDefaultValue().isPresent()) {
                AnnotationVisitor av0 = mv.visitParameterAnnotation(index, "Lradium/annotations/Nullable;", false);
                av0.visitEnd();
            } else {
                AnnotationVisitor av0 = mv.visitParameterAnnotation(index, "Lradium/annotations/NotNull;", false);
                av0.visitEnd();
            }
        });

    }

    private void appendReturnIfNotExists(Function function, Block block, StatementGenerator statementScopeGenerator) {
        boolean isLastStatementReturn = false;
        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            isLastStatementReturn = lastStatement.isReturnComplete();
        }
        if (!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenerator);
        }
    }

    private static class AccessorInterceptorFilter extends StatementGeneratorFilter {
        private final Field field;
        private final InstructionAdapter adapter;

        private AccessorInterceptorFilter(Field field, InstructionAdapter adapter, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.field = field;
            this.adapter = adapter;
        }

        @Override
        public void generate(FieldAssignment assignment, StatementGenerator generator) {
            if (field.equals(assignment.getField())) {
                PropertyAccessorsGenerator.generateNoPropertyTransformation(assignment, generator, adapter);
                next.generate(new EmptyExpression(assignment.getNodeData(), assignment.getField().getType()));
            } else {
                next.generate(assignment, generator);
            }
        }

        @Override
        public void generate(FieldReference fieldReference, StatementGenerator generator) {
            if (field.equals(fieldReference.getField())) {
                PropertyAccessorsGenerator.generateNoPropertyTransformation(fieldReference, generator, adapter);
                next.generate(new EmptyExpression(fieldReference.getNodeData(), fieldReference.getType()));
            } else {
                next.generate(fieldReference, generator);
            }
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new AccessorInterceptorFilter(field, adapter, parent, this.next, getScope());
        }
    }
}
