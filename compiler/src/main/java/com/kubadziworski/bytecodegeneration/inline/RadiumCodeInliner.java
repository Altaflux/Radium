package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.MethodNode;


public class RadiumCodeInliner implements CodeInliner {

    public static CodeInliner INSTANCE = new RadiumCodeInliner();

    private RadiumCodeInliner() {

    }

    @Override
    public void inlineMethod(Type currentClass, InstructionAdapter visitor, FunctionCall functionCall) {

        String newClass = currentClass.getName();
        String oldClass = functionCall.getSignature().getOwner().getName();

        String methodDescriptor = DescriptorFactory.getMethodDescriptor(functionCall.getSignature());
        EnkelType enkelType = ((EnkelType) (functionCall.getOwnerType()));
        Scope scope = enkelType.getScope();

        Function function = scope.getMethods().stream()
                .filter(func -> func.getName().equals(func.getName()))
                .filter(func -> {
                    String desc = DescriptorFactory.getMethodDescriptor(func.getFunctionSignature());
                    return desc.equals(methodDescriptor);
                })
                .findFirst().orElseThrow(() -> new CompilationException("Could not find method: " + functionCall.getName()));


        Block block = (Block) function.getRootStatement();
        Scope blockScope = block.getScope();
        String ownerDescriptor = functionCall.getSignature().getOwner().getAsmType().getInternalName();
        MethodNode methodNode = new MethodNode(Opcodes.ASM5, functionCall.getSignature().getModifiers(), functionCall.getName()
                , methodDescriptor, null, null);

        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodNode), blockScope);
        block.accept(statementScopeGenerator);

        MethodCallInliner methodCallInliner = new MethodCallInliner(methodNode.access, methodNode.desc, visitor, methodNode, oldClass, ownerDescriptor, newClass);

        int callOpCode = functionCall.getInvokeOpcode();
        methodCallInliner.visitMethodInsn(callOpCode, ownerDescriptor, functionCall.getName(), methodDescriptor, false);

    }

}
