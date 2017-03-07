package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.scope.FunctionScope;
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
    public MethodNode getMethodNode(Type owner, String name, String desc) {
        EnkelType enkelType = (EnkelType) (owner);
        Scope scope = enkelType.getScope();

        Function function = scope.getMethods().stream()
                .filter(func -> func.getName().equals(name))
                .filter(func -> DescriptorFactory.getMethodDescriptor(func.getFunctionSignature()).equals(desc))
                .findFirst().orElseThrow(() -> new CompilationException("Could not find method: " + name));

        Block block = (Block) function.getRootStatement();
        FunctionScope blockScope = block.getScope();

        int modifiers = ModifierTransformer.transform(function.getModifiers());
        MethodNode methodNode = new MethodNode(Opcodes.ASM5, modifiers, function.getName()
                , desc, null, null);

        MethodInliner methodInliner = new MethodInliner(modifiers, desc, methodNode,
                function.getFunctionSignature().getOwner().getName());

        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodInliner), blockScope);
        block.accept(statementScopeGenerator);

        return methodNode;
    }

}
